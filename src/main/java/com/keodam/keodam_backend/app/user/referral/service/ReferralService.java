package com.keodam.keodam_backend.app.user.referral.service;

import com.keodam.keodam_backend.app.user.domain.User;
import com.keodam.keodam_backend.app.user.referral.domain.Referral;
import com.keodam.keodam_backend.app.user.referral.repository.ReferralRepository;
import com.keodam.keodam_backend.app.user.repository.UserRepository;
import com.keodam.keodam_backend.exception.GeneralException;
import com.keodam.keodam_backend.global.code.status.ErrorStatus;
import com.keodam.keodam_backend.mypage.payment.service.BeanWalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReferralService {

    private final UserRepository userRepository;
    private final ReferralRepository referralRepository;
    private final BeanWalletService beanWalletService;

    public String registerReferral(User sponsor, String refereeNickname) {

        if (referralRepository.findBySponsor(sponsor).isPresent()) {
            throw new GeneralException(ErrorStatus.ALREADY_REGISTER_REFERRAL);
        }

        User referee = userRepository.findByNickname(refereeNickname)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        if (sponsor.getId().equals(referee.getId())) {
            throw new GeneralException(ErrorStatus.CANNOT_REFER_SELF);
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
            throw new GeneralException(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
        return "추천이 성공적으로 등록되었습니다.";
    }
}
