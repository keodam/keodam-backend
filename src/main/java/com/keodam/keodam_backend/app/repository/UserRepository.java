package com.keodam.keodam_backend.app.repository;

import com.keodam.keodam_backend.app.domain.User;
import com.keodam.keodam_backend.app.phone.domain.UserIdentityInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Transactional(readOnly = true)
    Optional<User> findByEmail(String email);
    @Transactional(readOnly = true)
    Optional<User> findByRefreshToken(String refreshToken);
}