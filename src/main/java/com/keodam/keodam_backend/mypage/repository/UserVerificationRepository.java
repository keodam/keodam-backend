package com.keodam.keodam_backend.mypage.repository;

import com.keodam.keodam_backend.mypage.domain.UserVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserVerificationRepository extends JpaRepository<UserVerification, Long> {
    Optional<UserVerification> findByUser_Email(String email);
}
