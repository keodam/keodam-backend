package com.keodam.keodam_backend.app.phone.service;

import com.keodam.keodam_backend.app.phone.domain.UserIdentityInfo;
import com.keodam.keodam_backend.app.phone.dto.UserVerifyCheckRequestDto;
import com.keodam.keodam_backend.app.phone.dto.UserVerifyCodeRequestDto;
import com.keodam.keodam_backend.app.phone.repository.UserIdentityInfoRepository;
import com.keodam.keodam_backend.global.config.TwilioConfig;
import com.keodam.keodam_backend.global.util.TwilioUtils;
import com.twilio.exception.ApiException;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class UserAuthenticateService {

    private final TwilioConfig twilioConfig;
    private final UserIdentityInfoRepository userIdentityInfoRepository;
    // 휴대폰별 최근 인증 요청 시간 저장 (메모리캐시)
    private final Map<String, LocalDateTime> recentRequests = new ConcurrentHashMap<>();

    @io.swagger.v3.oas.annotations.Operation(summary = "인증번호 요청", description = "사용자의 휴대폰 번호로 인증번호를 발송합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "인증번호 발송 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "입력값 오류 또는 인증 요청 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "429", description = "2분 이내 재요청 시도")
    })

    public ResponseEntity<Object> startVerification(UserVerifyCodeRequestDto dto) {
        String phone = dto.getPhoneNumber();
        String e164 = TwilioUtils.formatPhone(phone);

        // 휴대폰번호 유효성
        if (!phone.startsWith("010") || phone.length() != 11) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("휴대폰 번호는 010으로 시작하는 11자리여야 합니다.");
        }

        // 이름 유효성
        if (dto.getUserRealName() == null || !dto.getUserRealName().matches("^[A-Za-z]{1,12}$|^[가-힣]{1,6}$")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("이름은 한글 1~6자 또는 영문 1~12자만 입력 가능합니다.");
        }

        // 생년월일 유효성
        if (!dto.getUserBirth().matches("^\\d{6}$")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("생년월일은 6자리 숫자여야 합니다.");
        }

        // 2분 내 중복 요청 제한
        LocalDateTime last = recentRequests.get(phone);
        if (last != null && Duration.between(last, LocalDateTime.now()).toMinutes() < 2) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("2분 이내에는 인증번호를 다시 요청할 수 없습니다.");
        }

        try {
            Verification.creator(twilioConfig.getServiceSid(), e164, "sms").create();
            recentRequests.put(phone, LocalDateTime.now());
            return ResponseEntity.ok("인증번호 발송 성공. 유효시간 3분 내 입력해주세요.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("인증 요청 실패: " + e.getMessage());
        }
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "인증번호 검증", description = "사용자가 입력한 인증번호를 검증하고 성공 시 사용자 정보를 저장 또는 갱신합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "인증 성공 및 사용자 정보 저장 또는 업데이트 완료"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "입력값 오류 또는 Twilio 인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패: 잘못된 코드")
    })
    public ResponseEntity<Object> checkVerification(UserVerifyCheckRequestDto dto) {
        String phone = dto.getPhoneNumber();
        String e164 = TwilioUtils.formatPhone(phone);

        // 이름 유효성 (한글 1~6자 또는 영문 1~12자)
        if (dto.getUserRealName() == null || !dto.getUserRealName().matches("^[A-Za-z]{1,12}$|^[가-힣]{1,6}$")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이름은 한글 1~6자 또는 영문 1~12자만 입력 가능합니다.");
        }

        // 생년월일 6자리 숫자만 허용
        if (!dto.getUserBirth().matches("^\\d{6}$")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("생년월일은 6자리 숫자여야 합니다.");
        }

        try {
            VerificationCheck check = VerificationCheck.creator(twilioConfig.getServiceSid())
                    .setTo(e164)
                    .setCode(dto.getCode())
                    .create();

            if ("approved".equals(check.getStatus())) {
                Optional<UserIdentityInfo> existing = userIdentityInfoRepository.findByPhoneNumber(phone);
                if (existing.isPresent()) {
                    UserIdentityInfo user = existing.get();
                    user.updateInfo(
                            dto.getUserBirth(),
                            dto.getUserRealName(),
                            dto.getUserGender()
                    );
                    user.setVerifiedAt(LocalDateTime.now());
                    userIdentityInfoRepository.save(user);
                    return ResponseEntity.ok("기존 사용자 정보 업데이트 완료");
                } else {
                    UserIdentityInfo user = UserIdentityInfo.builder()
                            .phoneNumber(phone)
                            .userRealName(dto.getUserRealName())
                            .userBirth(dto.getUserBirth())
                            .userGender(dto.getUserGender())
                            .verifiedAt(LocalDateTime.now())
                            .isActive(true)
                            .build();
                    userIdentityInfoRepository.save(user);
                    return ResponseEntity.ok("인증 성공 및 사용자 정보 저장 완료");
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 실패: 잘못된 코드입니다.");
            }

        } catch (ApiException e) {
            if (e.getStatusCode() == 404) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("유효하지 않거나 만료된 인증 요청입니다.");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("검증 실패: " + e.getMessage());
        }
    }
}
