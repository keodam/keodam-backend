package com.keodam.keodam_backend.mypage.payment.repository;

import com.keodam.keodam_backend.app.user.domain.User;
import com.keodam.keodam_backend.mypage.payment.domain.BeanWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BeanWalletRepository extends JpaRepository<BeanWallet, Long> {
    Optional<BeanWallet> findByUser(User user);
    void deleteByUser(User user);
}
