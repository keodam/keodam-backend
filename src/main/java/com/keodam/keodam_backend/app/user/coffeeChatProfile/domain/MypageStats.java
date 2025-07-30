package com.keodam.keodam_backend.app.user.coffeeChatProfile.domain;

import com.keodam.keodam_backend.app.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "mypage_stats")
public class MypageStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "exp_point")
    private Integer expPoint;

    @Column(name = "manner_temperature")
    private Double mannerTemperature;

    @Builder
    public MypageStats(User user) {
        this.user = user;
        this.expPoint = 0;
        this.mannerTemperature = 36.5;
    }

    public void addExpPoint(int amount) {
        if (this.expPoint == null) {
            this.expPoint = 0;
        }
        this.expPoint += amount;
    }
}
