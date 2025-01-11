package com.keodam.keodam_backend.app.repository;

import com.keodam.keodam_backend.app.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByRefreshToken(String refreshToken);
}