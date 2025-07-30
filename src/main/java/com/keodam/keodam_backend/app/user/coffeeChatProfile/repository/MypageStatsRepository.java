package com.keodam.keodam_backend.app.user.coffeeChatProfile.repository;

import com.keodam.keodam_backend.app.user.coffeeChatProfile.domain.MypageStats;
import com.keodam.keodam_backend.app.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MypageStatsRepository extends JpaRepository<MypageStats, Long> {
    Optional<MypageStats> findByUser(User user);
}
