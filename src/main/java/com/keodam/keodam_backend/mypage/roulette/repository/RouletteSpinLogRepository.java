package com.keodam.keodam_backend.mypage.roulette.repository;

import com.keodam.keodam_backend.mypage.roulette.domain.RouletteSpinLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouletteSpinLogRepository extends JpaRepository<RouletteSpinLog, Long> {
}
