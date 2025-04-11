package com.keodam.keodam_backend.mypage.domain;

import com.keodam.keodam_backend.app.domain.User;
import jakarta.persistence.*;

public class Payout {

    @Id
    @Column(name = "order_id", nullable = false)
    private String orderId;

    @ManyToOne
    User mentor;

    @Column(name = "price", nullable = false)
    private int price;

    @Enumerated(EnumType.STRING)
    private PayoutStatus payoutStatus;

    @OneToOne
    Payout payout;
}
