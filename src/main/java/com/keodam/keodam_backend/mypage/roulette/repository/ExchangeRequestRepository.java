package com.keodam.keodam_backend.mypage.roulette.repository;

import com.keodam.keodam_backend.mypage.roulette.domain.ExchangeRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExchangeRequestRepository extends JpaRepository<ExchangeRequest, Long> {
}
