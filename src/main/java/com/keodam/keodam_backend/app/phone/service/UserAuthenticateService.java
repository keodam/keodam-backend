package com.keodam.keodam_backend.app.phone.service;

import com.keodam.keodam_backend.app.domain.User;
import com.keodam.keodam_backend.app.phone.domain.UserIdentityInfo;
import com.keodam.keodam_backend.app.phone.dto.UserVerifyCheckRequestDto;
import com.keodam.keodam_backend.app.phone.dto.UserVerifyCodeRequestDto;
import com.keodam.keodam_backend.app.phone.repository.UserIdentityInfoRepository;

import com.keodam.keodam_backend.app.user.repository.UserRepository;
import com.keodam.keodam_backend.global.code.status.ErrorStatus;
import com.keodam.keodam_backend.global.code.status.SuccessStatus;
import com.keodam.keodam_backend.global.config.TwilioConfig;
import com.keodam.keodam_backend.global.util.TwilioUtils;
import com.twilio.exception.ApiException;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import java.util.List;
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
        if (last != null && Duration.between(last, LocalDateTime.now()).toMinutes() < 1) {
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

            if (existing.isPresent()) {
                UserIdentityInfo oldInfo = existing.get();

                // 7일 이내 탈퇴 재가입 제한
                if (!oldInfo.getIsActive() && oldInfo.getDeletedAt() != null &&
                        Duration.between(oldInfo.getDeletedAt(), LocalDateTime.now()).toDays() < 7) {
                    return ResponseEntity.status(ErrorStatus.BAD_REQUEST.getHttpStatus())
                            .body(ErrorStatus.BAD_REQUEST.getReasonHttpStatus());
                }

                // 기 존재 유저의 인증 재요청 케이스 : 정보업데이트
                if (oldInfo.getUser() != null && oldInfo.getUser().getId().equals(user.getId())) {
                    oldInfo.updateInfo(dto.getUserBirth(), dto.getUserRealName(), dto.getUserGender());
                    oldInfo.markVerifiedNow();
                    userIdentityInfoRepository.save(oldInfo);

                    return ResponseEntity.status(SuccessStatus._OK.getHttpStatus())
                            .body(SuccessStatus._OK.getReasonHttpStatus());
                }

                // 다른 유저와 연결된 인증 정보에 요청한 케이스 : unlink + soft delete + number archiving
                if (oldInfo.getUser() != null && !oldInfo.getUser().getId().equals(user.getId())) {
                    String originalPhone = oldInfo.getPhoneNumber();
                    String baseArchivedPhone = "ARCHIVED-" + originalPhone;

                    List<String> archivedPhones = userIdentityInfoRepository.findAllArchivedPhones(baseArchivedPhone);
                    int maxSuffix = 0;
                    for (String archived : archivedPhones) {
                        if (archived.equals(baseArchivedPhone)) {
                            maxSuffix = Math.max(maxSuffix, 1);
                        } else if (archived.startsWith(baseArchivedPhone + "-")) {
                            try {
                                int suffix = Integer.parseInt(archived.substring((baseArchivedPhone + "-").length()));
                                maxSuffix = Math.max(maxSuffix, suffix);
                            } catch (NumberFormatException ignored) {}
                        }
                    }

                    int nextSuffix = maxSuffix + 1;
                    if (nextSuffix > 1000) {
                        return ResponseEntity
                                .status(ErrorStatus.BAD_REQUEST.getHttpStatus())
                                .body(ErrorStatus.BAD_REQUEST.getReasonHttpStatus());
                    }

                    String archivedPhone = nextSuffix == 1 ? baseArchivedPhone : baseArchivedPhone + "-" + nextSuffix;

                    oldInfo.unlinkUser();
                    oldInfo.deactivate("NEWUSER:휴대폰 번호 갱신됨");
                    oldInfo.setDeletedAt(LocalDateTime.now());
                    oldInfo.setPhoneNumber(archivedPhone);

                    userIdentityInfoRepository.save(oldInfo);
                }
            }
            // 새로운 인증 정보 생성
            UserIdentityInfo newInfo = UserIdentityInfo.builder()
                    .phoneNumber(phone)
                    .userRealName(dto.getUserRealName())
                    .userBirth(dto.getUserBirth())
                    .userGender(dto.getUserGender())
                    .verifiedAt(LocalDateTime.now())
                    .isActive(true)
                    .build();

            newInfo.linkUser(user);
            userIdentityInfoRepository.save(newInfo);

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
