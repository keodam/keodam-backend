package com.keodam.keodam_backend.app.service;

import com.keodam.keodam_backend.app.domain.UserIdentityInfo;
import com.keodam.keodam_backend.app.dto.UserVerifyCheckRequestDto;
import com.keodam.keodam_backend.app.dto.UserVerifyCodeRequestDto;
import com.keodam.keodam_backend.app.repository.UserIdentityInfoRepository;
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

    public ResponseEntity<Object> startVerification(UserVerifyCodeRequestDto dto) {
        String phone = dto.getPhoneNumber();
        String e164 = TwilioUtils.formatPhone(phone);

        LocalDateTime last = recentRequests.get(phone);
        if (last != null && Duration.between(last, LocalDateTime.now()).toMinutes() < 2) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("2분 이내에는 인증번호를 다시 요청할 수 없습니다.");
        }

        try {
            Verification.creator(twilioConfig.getServiceSid(), e164, "sms").create();
            recentRequests.put(phone, LocalDateTime.now());
            return ResponseEntity.ok("인증번호 발송 성공");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("인증 요청 실패: " + e.getMessage());
        }
    }

    public ResponseEntity<Object> checkVerification(UserVerifyCheckRequestDto dto) {
        String phone = dto.getPhoneNumber();
        String e164 = TwilioUtils.formatPhone(phone);

        try {
            VerificationCheck check = VerificationCheck.creator(twilioConfig.getServiceSid())
                    .setTo(e164)
                    .setCode(dto.getCode())
                    .create();

            if ("approved".equals(check.getStatus())) {
                Optional<UserIdentityInfo> existing = userIdentityInfoRepository.findByPhoneNumber(phone);
                if (existing.isPresent()) {
                    UserIdentityInfo user = existing.get();
                    user.updateInfo(dto.getUserBirth(), dto.getUserName(), dto.getUserRealName(), dto.getUserGender());
                    user.setVerifiedAt(LocalDateTime.now());
                    userIdentityInfoRepository.save(user);
                    return ResponseEntity.ok("기존 사용자 정보 업데이트 완료");
                } else {
                    UserIdentityInfo user = UserIdentityInfo.builder()
                            .phoneNumber(phone)
                            .userRealName(dto.getUserRealName())
                            .userBirth(dto.getUserBirth())
                            .userName(dto.getUserName())
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
