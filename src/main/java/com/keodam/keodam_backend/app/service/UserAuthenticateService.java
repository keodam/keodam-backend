package com.keodam.keodam_backend.app.service;

import com.keodam.keodam_backend.app.domain.UserIdentityInfo;
import com.keodam.keodam_backend.app.dto.UserVerifyCodeRequestDto;
import com.keodam.keodam_backend.app.dto.UserVerifyCheckRequestDto;
import com.keodam.keodam_backend.app.repository.UserIdentityInfoRepository;
import com.keodam.keodam_backend.global.config.TwilioConfig;
import com.keodam.keodam_backend.global.util.TwilioUtils;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAuthenticateService {

    private final TwilioConfig twilioConfig;
    private final UserIdentityInfoRepository userIdentityInfoRepository;

    public ResponseEntity<Object> startVerification(UserVerifyCodeRequestDto dto) {
        String e164 = TwilioUtils.formatPhone(dto.getPhoneNumber());
        try {
            Verification.creator(twilioConfig.getServiceSid(), e164, "sms").create();
            return ResponseEntity.ok("인증번호 발송 성공");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("인증 요청 실패: " + e.getMessage());
        }
    }

    public ResponseEntity<Object> checkVerification(UserVerifyCheckRequestDto dto) {
        String e164 = TwilioUtils.formatPhone(dto.getPhoneNumber());
        try {
            VerificationCheck check = VerificationCheck.creator(twilioConfig.getServiceSid())
                    .setTo(e164)
                    .setCode(dto.getCode())
                    .create();

            if ("approved".equals(check.getStatus())) {
                saveUserIdentityInfo(dto);
                return ResponseEntity.ok("인증 성공 및 사용자 정보 저장 완료");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 실패");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("검증 실패: " + e.getMessage());
        }
    }

    private void saveUserIdentityInfo(UserVerifyCheckRequestDto dto) {
        UserIdentityInfo userInfo = UserIdentityInfo.builder()
                .phoneNumber(dto.getPhoneNumber())
                .userSex(dto.getUserSex())
                .userRealName(dto.getUserRealName())
                .userBirth(dto.getUserBirth())
                .userName(dto.getUserName())
                .build();

        userIdentityInfoRepository.save(userInfo);
    }
}
