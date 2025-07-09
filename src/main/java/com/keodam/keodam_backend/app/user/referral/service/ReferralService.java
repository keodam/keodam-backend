package com.keodam.keodam_backend.app.user.referral.service;

import com.keodam.keodam_backend.app.domain.User;
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

    public void registerReferral(Long inviteeId, String referrerNickname){
        User invitee = userRepository.findById(inviteeId).orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        if (referralRepository.findByReferrer(invitee).isPresent()){
            throw new GeneralException(ErrorStatus.ALREADY_REGISTER_REFERRAL);
        }

        User referrer = userRepository.findByNickname(referrerNickname)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        if (invitee.getId().equals(referrer.getId())){
            throw new GeneralException(ErrorStatus.CANNOT_REFER_SELF);
        }

        Referral referral = Referral.builder()
                .invitee(invitee)
                .referrer(referrer)
                .build();

        referralRepository.save(referral);


        beanWalletService.rewardReferralBeans(invitee, 100);
        beanWalletService.rewardReferralBeans(referrer, 100);
    }
}
