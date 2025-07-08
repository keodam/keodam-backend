package com.keodam.keodam_backend.mypage.payment.repository;

import com.keodam.keodam_backend.mypage.payment.domain.BeanTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface beanTransactionRepository extends JpaRepository<BeanTransaction, Long> {
}
