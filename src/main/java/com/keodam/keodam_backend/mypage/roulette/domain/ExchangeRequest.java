package com.keodam.keodam_backend.mypage.roulette.domain;

import com.keodam.keodam_backend.app.user.domain.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "exchange_request")
@Getter
@NoArgsConstructor
public class ExchangeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExchangeRequestStatus status;

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    @Builder
    public ExchangeRequest(User user, String phoneNumber, ExchangeRequestStatus status) {
        this.user = user;
        this.phoneNumber = phoneNumber;
        this.status = status;
        this.requestedAt = LocalDateTime.now();
    }
}
