package com.keodam.keodam_backend.mypage.roulette.repository;

import com.keodam.keodam_backend.app.user.domain.User;
import com.keodam.keodam_backend.mypage.roulette.domain.RouletteSpinLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RouletteSpinLogRepository extends JpaRepository<RouletteSpinLog, Long> {
    void deleteByUser(User user);
}
