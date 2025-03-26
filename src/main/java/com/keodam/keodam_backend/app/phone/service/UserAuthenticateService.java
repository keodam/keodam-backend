package com.keodam.keodam_backend.app.phone.service;

import com.keodam.keodam_backend.app.domain.User;
import com.keodam.keodam_backend.app.phone.domain.UserIdentityInfo;
import com.keodam.keodam_backend.app.phone.dto.UserVerifyCheckRequestDto;
import com.keodam.keodam_backend.app.phone.dto.UserVerifyCodeRequestDto;
import com.keodam.keodam_backend.app.phone.repository.UserIdentityInfoRepository;
import com.keodam.keodam_backend.app.repository.UserRepository;
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
    private final UserRepository userRepository;

    // 휴대폰별 최근 인증 요청 시간 저장 (메모리캐시)
    private final Map<String, LocalDateTime> recentRequests = new ConcurrentHashMap<>();

    public ResponseEntity<Object> startVerification(UserVerifyCodeRequestDto dto, String email) {
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("인증 정보가 없습니다. 로그인 후 시도해주세요.");
        }

        String phone = dto.getPhoneNumber();
        String e164 = TwilioUtils.formatPhone(phone);
        // 번호유효성검증
        if (!phone.startsWith("010") || phone.length() != 11) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("휴대폰 번호는 010으로 시작하는 11자리여야 합니다.");
        }
        // 이름유효성검증
        if (dto.getUserRealName() == null || !dto.getUserRealName().matches("^[A-Za-z]{1,12}$|^[가-힣]{1,6}$")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("이름은 한글 1~6자 또는 영문 1~12자만 입력 가능합니다.");
        }
        // 생년월일유효성검증
        if (!dto.getUserBirth().matches("^\\d{6}$")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("생년월일은 6자리 숫자여야 합니다.");
        }
        // 2분내중복요청제한
        LocalDateTime last = recentRequests.get(phone);
        if (last != null && Duration.between(last, LocalDateTime.now()).toMinutes() < 2) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("3분 이내에는 인증번호를 다시 요청할 수 없습니다.");
        }

        try {
            Verification.creator(twilioConfig.getServiceSid(), e164, "sms").create();
            recentRequests.put(phone, LocalDateTime.now());
            return ResponseEntity.ok("인증번호 발송 성공. 유효시간 3분 내 입력해주세요.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("인증 요청 실패: " + e.getMessage());
        }
    }

    public ResponseEntity<Object> checkVerification(UserVerifyCheckRequestDto dto, String email) {
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("인증 정보가 없습니다. 로그인 후 시도해주세요.");
        }

        String phone = dto.getPhoneNumber();
        String e164 = TwilioUtils.formatPhone(phone);
        // 이름유효성검증
        if (dto.getUserRealName() == null || !dto.getUserRealName().matches("^[A-Za-z]{1,12}$|^[가-힣]{1,6}$")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("이름은 한글 1~6자 또는 영문 1~12자만 입력 가능합니다.");
        }
        // 생년월일유효성검증
        if (!dto.getUserBirth().matches("^\\d{6}$")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("생년월일은 6자리 숫자여야 합니다.");
        }

        try {
            VerificationCheck check = VerificationCheck.creator(twilioConfig.getServiceSid())
                    .setTo(e164)
                    .setCode(dto.getCode())
                    .create();

            if ("approved".equals(check.getStatus())) {
                User user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다."));

                Optional<UserIdentityInfo> existing = userIdentityInfoRepository.findByPhoneNumber(phone);
                UserIdentityInfo userInfo;

                if (existing.isPresent()) {
                    userInfo = existing.get();
                    userInfo.updateInfo(dto.getUserBirth(), dto.getUserRealName(), dto.getUserGender());
                    userInfo.markVerifiedNow();
                    userIdentityInfoRepository.save(userInfo);
                    // 기존에 연결된 다른 사용자 정보가 있다면 해제
                    Optional<User> otherUser = userRepository.findByIdentityInfo(userInfo);
                    if (otherUser.isPresent() && !otherUser.get().getEmail().equals(user.getEmail())) {
                        otherUser.get().unlinkIdentityInfo();
                        userRepository.save(otherUser.get());
                    }

                } else {
                    userInfo = UserIdentityInfo.builder()
                            .phoneNumber(phone)
                            .userRealName(dto.getUserRealName())
                            .userBirth(dto.getUserBirth())
                            .userGender(dto.getUserGender())
                            .verifiedAt(LocalDateTime.now())
                            .isActive(true)
                            .build();
                    userIdentityInfoRepository.save(userInfo);
                }

                user.linkIdentityInfo(userInfo);
                userRepository.save(user);

                return ResponseEntity.ok("인증 성공 및 사용자 정보 저장 완료");
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
