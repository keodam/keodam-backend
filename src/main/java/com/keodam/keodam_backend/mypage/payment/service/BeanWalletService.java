package com.keodam.keodam_backend.mypage.payment.service;

import com.keodam.keodam_backend.app.domain.User;
import com.keodam.keodam_backend.mypage.payment.domain.BeanTransaction;
import com.keodam.keodam_backend.mypage.payment.domain.BeanTransactionType;
import com.keodam.keodam_backend.mypage.payment.domain.BeanWallet;
import com.keodam.keodam_backend.mypage.payment.repository.BeanTransactionRepository;
import com.keodam.keodam_backend.mypage.payment.repository.BeanWalletRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BeanWalletService {

    private final BeanWalletRepository beanWalletRepository;
    private final BeanTransactionRepository beanTransactionRepository;

    @Transactional
    public void rewardReferralBeans(User user, int amount){
        BeanWallet wallet = beanWalletRepository.findByUser(user).orElseGet(() -> createWalletForUser(user));
        wallet.increaseReferral(amount);
        beanWalletRepository.save(wallet);
        beanTransactionRepository.save(
                BeanTransaction.builder()
                        .user(user)
                        .wallet(wallet)
                        .beanAmount(amount)
                        .type(BeanTransactionType.REFERRAL_REWARD)
                .build()
        );
    }

    private BeanWallet createWalletForUser(User user){
        return beanWalletRepository.save(
                BeanWallet.builder().user(user).totalBeans(0).build()
        );
    }
}
