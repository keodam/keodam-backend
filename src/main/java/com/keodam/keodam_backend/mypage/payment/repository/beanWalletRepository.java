package com.keodam.keodam_backend.mypage.payment.repository;

import com.keodam.keodam_backend.app.domain.User;
import com.keodam.keodam_backend.mypage.payment.domain.BeanWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface beanWalletRepository extends JpaRepository<BeanWallet, Long> {
    Optional<BeanWallet> findByUser(User user);
}
