package com.keodam.keodam_backend.app.user.coffeechatprofile.repository;

import com.keodam.keodam_backend.app.user.coffeechatprofile.domain.MypageStats;
import com.keodam.keodam_backend.app.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MypageStatsRepository extends JpaRepository<MypageStats, Long> {
    Optional<MypageStats> findByUser(User user);
    void deleteByUser(User user);
}
