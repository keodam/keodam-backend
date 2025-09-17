package com.keodam.keodam_backend.mypage.payment.repository;

import com.keodam.keodam_backend.app.user.domain.User;
import com.keodam.keodam_backend.mypage.payment.domain.BeanTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BeanTransactionRepository extends JpaRepository<BeanTransaction, Long> {
    void deleteByUser(User user);
}
