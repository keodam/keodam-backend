package com.keodam.keodam_backend.app.user.referral.service;

import com.keodam.keodam_backend.app.domain.User;
import com.keodam.keodam_backend.app.user.referral.domain.Referral;
import com.keodam.keodam_backend.app.user.referral.repository.ReferralRepository;
import com.keodam.keodam_backend.app.user.repository.UserRepository;
import com.keodam.keodam_backend.global.code.status.ErrorStatus;
import com.keodam.keodam_backend.global.code.status.SuccessStatus;
import com.keodam.keodam_backend.mypage.payment.service.BeanWalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReferralService {

    private final UserRepository userRepository;
    private final ReferralRepository referralRepository;
    private final BeanWalletService beanWalletService;

    public ResponseEntity<Object> registerReferral(User sponsor, String refereeNickname) {
        if (referralRepository.findBySponsor(sponsor).isPresent()) {
            return ResponseEntity.status(ErrorStatus.ALREADY_REGISTER_REFERRAL.getHttpStatus())
                    .body(ErrorStatus.ALREADY_REGISTER_REFERRAL.getReasonHttpStatus());
        }

        User referee = userRepository.findByNickname(refereeNickname)
                .orElse(null);

        if (referee == null) {
            return ResponseEntity.status(ErrorStatus.USER_NOT_FOUND.getHttpStatus())
                    .body(ErrorStatus.USER_NOT_FOUND.getReasonHttpStatus());
        }

        if (sponsor.getId().equals(referee.getId())) {
            return ResponseEntity.status(ErrorStatus.CANNOT_REFER_SELF.getHttpStatus())
                    .body(ErrorStatus.CANNOT_REFER_SELF.getReasonHttpStatus());
        }

        Referral referral = Referral.builder()
                .sponsor(sponsor)
                .referee(referee)
                .build();
        referralRepository.save(referral);

        try {
            beanWalletService.rewardReferralBeans(sponsor, 100);
            beanWalletService.rewardReferralBeans(referee, 100);
        } catch (Exception e) {
            return ResponseEntity.status(ErrorStatus.INTERNAL_SERVER_ERROR.getHttpStatus())
                    .body(ErrorStatus.INTERNAL_SERVER_ERROR.getReasonHttpStatus());
        }

        return ResponseEntity.status(SuccessStatus._OK.getHttpStatus())
                .body(SuccessStatus._OK.getReasonHttpStatus());
    }
}
