package com.keodam.keodam_backend.mypage.domain;

import com.keodam.keodam_backend.app.domain.User;
import jakarta.persistence.*;

@Entity
public class Payout {

    @Id
    @Column(name = "order_id", nullable = false)
    private String orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User mentor;

    @Column(name = "price", nullable = false)
    private int price;

    @Enumerated(EnumType.STRING)
    @Column(name = "payout_status")
    private PayoutStatus payoutStatus;

    @OneToOne
    @JoinColumn(name = "ticket_id")
    private CoffeeChatTicket coffeeChatTicket;
}
