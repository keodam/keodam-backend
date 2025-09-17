package com.keodam.keodam_backend.mypage.roulette.repository;

import com.keodam.keodam_backend.app.user.domain.User;
import com.keodam.keodam_backend.mypage.roulette.domain.ExchangeRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExchangeRequestRepository extends JpaRepository<ExchangeRequest, Long> {
    Optional<ExchangeRequest> findByUser(User user);
    void deleteByUser(User user);
}
