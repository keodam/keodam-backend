package com.keodam.keodam_backend.app.phone.service;

import com.keodam.keodam_backend.app.domain.User;
import com.keodam.keodam_backend.app.phone.domain.UserIdentityInfo;
import com.keodam.keodam_backend.app.phone.dto.UserVerifyCheckRequestDto;
import com.keodam.keodam_backend.app.phone.dto.UserVerifyCodeRequestDto;
import com.keodam.keodam_backend.app.phone.repository.UserIdentityInfoRepository;
import com.keodam.keodam_backend.app.repository.UserRepository;
import com.keodam.keodam_backend.global.code.status.ErrorStatus;
import com.keodam.keodam_backend.global.code.status.SuccessStatus;
import com.keodam.keodam_backend.global.config.TwilioConfig;
import com.keodam.keodam_backend.global.util.TwilioUtils;
import com.twilio.exception.ApiException;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import lombok.RequiredArgsConstructor;
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

    private final Map<String, LocalDateTime> recentRequests = new ConcurrentHashMap<>();

    public ResponseEntity<Object> startVerification(UserVerifyCodeRequestDto dto, String email) {
        if (email == null) {
            return ResponseEntity.status(ErrorStatus.UNAUTHORIZED.getHttpStatus())
                    .body(ErrorStatus.UNAUTHORIZED.getReasonHttpStatus());
        }

        String phone = dto.getPhoneNumber();
        String e164 = TwilioUtils.formatPhone(phone);

        if (!phone.startsWith("010") || phone.length() != 11) {
            return ResponseEntity.status(ErrorStatus.INVALID_PHONE_FORMAT.getHttpStatus())
                    .body(ErrorStatus.INVALID_PHONE_FORMAT.getReasonHttpStatus());
        }

        if (dto.getUserRealName() == null || !dto.getUserRealName().matches("^[A-Za-z]{1,12}$|^[가-힣]{1,6}$")) {
            return ResponseEntity.status(ErrorStatus.NAME_INVALID.getHttpStatus())
                    .body(ErrorStatus.NAME_INVALID.getReasonHttpStatus());
        }

        if (!dto.getUserBirth().matches("^\\d{6}$")) {
            return ResponseEntity.status(ErrorStatus.BIRTH_INVALID.getHttpStatus())
                    .body(ErrorStatus.BIRTH_INVALID.getReasonHttpStatus());
        }

        LocalDateTime last = recentRequests.get(phone);
        if (last != null && Duration.between(last, LocalDateTime.now()).toMinutes() < 2) {
            return ResponseEntity.status(ErrorStatus.TOO_MANY_REQUEST.getHttpStatus())
                    .body(ErrorStatus.TOO_MANY_REQUEST.getReasonHttpStatus());
        }

        try {
            Verification.creator(twilioConfig.getServiceSid(), e164, "sms").create();
            recentRequests.put(phone, LocalDateTime.now());
            return ResponseEntity.status(SuccessStatus._OK.getHttpStatus())
                    .body(SuccessStatus._OK.getReasonHttpStatus());
        } catch (Exception e) {
            return ResponseEntity.status(ErrorStatus.BAD_REQUEST.getHttpStatus())
                    .body(ErrorStatus.BAD_REQUEST.getReasonHttpStatus());
        }
    }

    public ResponseEntity<Object> checkVerification(UserVerifyCheckRequestDto dto, String email) {
        if (email == null) {
            return ResponseEntity.status(ErrorStatus.UNAUTHORIZED.getHttpStatus())
                    .body(ErrorStatus.UNAUTHORIZED.getReasonHttpStatus());
        }

        String phone = dto.getPhoneNumber();
        String e164 = TwilioUtils.formatPhone(phone);

        if (dto.getUserRealName() == null || !dto.getUserRealName().matches("^[A-Za-z]{1,12}$|^[가-힣]{1,6}$")) {
            return ResponseEntity.status(ErrorStatus.NAME_INVALID.getHttpStatus())
                    .body(ErrorStatus.NAME_INVALID.getReasonHttpStatus());
        }

        if (!dto.getUserBirth().matches("^\\d{6}$")) {
            return ResponseEntity.status(ErrorStatus.BIRTH_INVALID.getHttpStatus())
                    .body(ErrorStatus.BIRTH_INVALID.getReasonHttpStatus());
        }

        try {
            VerificationCheck check = VerificationCheck.creator(twilioConfig.getServiceSid())
                    .setTo(e164)
                    .setCode(dto.getCode())
                    .create();

            if (!"approved".equals(check.getStatus())) {
                return ResponseEntity.status(ErrorStatus.VERIFICATION_FAILED.getHttpStatus())
                        .body(ErrorStatus.VERIFICATION_FAILED.getReasonHttpStatus());
            }

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다."));

            Optional<UserIdentityInfo> existing = userIdentityInfoRepository.findByPhoneNumber(phone);
            UserIdentityInfo userInfo;

            if (existing.isPresent()) {
                userInfo = existing.get();
                userInfo.updateInfo(dto.getUserBirth(), dto.getUserRealName(), dto.getUserGender());
                userInfo.markVerifiedNow();
                userIdentityInfoRepository.save(userInfo);

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

            return ResponseEntity.status(SuccessStatus._OK.getHttpStatus())
                    .body(SuccessStatus._OK.getReasonHttpStatus());

        } catch (ApiException e) {
            if (e.getStatusCode() == 404) {
                return ResponseEntity.status(ErrorStatus.VERIFICATION_FAILED.getHttpStatus())
                        .body(ErrorStatus.VERIFICATION_FAILED.getReasonHttpStatus());
            }
            return ResponseEntity.status(ErrorStatus.BAD_REQUEST.getHttpStatus())
                    .body(ErrorStatus.BAD_REQUEST.getReasonHttpStatus());
        } catch (Exception e) {
            return ResponseEntity.status(ErrorStatus.INTERNAL_SERVER_ERROR.getHttpStatus())
                    .body(ErrorStatus.INTERNAL_SERVER_ERROR.getReasonHttpStatus());
        }
    }
}
