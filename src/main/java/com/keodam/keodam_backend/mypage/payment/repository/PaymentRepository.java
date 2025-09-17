package com.keodam.keodam_backend.mypage.payment.repository;

import com.keodam.keodam_backend.app.user.domain.User;
import com.keodam.keodam_backend.mypage.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    boolean existsByReceiptId(String receiptId);
    void deleteByUser(User user);
}
