package com.keodam.keodam_backend.mypage.payment.service;

import com.keodam.keodam_backend.app.domain.User;
import com.keodam.keodam_backend.mypage.payment.domain.BeanWallet;
import com.keodam.keodam_backend.mypage.payment.repository.BeanTransactionRepository;
import com.keodam.keodam_backend.mypage.payment.repository.BeanWalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BeanWalletService {

    private final BeanWalletRepository beanWalletRepository;
    private final BeanTransactionRepository beanTransactionRepository;

    public void rewardReferralBeans(User user, int amount){
        // BeanWallet 조회, 추천 원두 증가, BeanTransaction 저장 (type REFERRAL_REWARD)
    }

    private BeanWallet createWalletForUser(User user){
        return beanWalletRepository.save(
                BeanWallet.builder().user(user).totalBeans(0).build()
        );
    }
}
