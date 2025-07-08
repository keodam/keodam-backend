package com.keodam.keodam_backend.mypage.payment.domain;

import com.keodam.keodam_backend.app.domain.User;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Payout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User mentor;

    @Column(name = "price", nullable = false)
    private int price;

    @Enumerated(EnumType.STRING)
    @Column(name = "payout_status")
    private PayoutStatus payoutStatus;

    @OneToMany(mappedBy = "payout", cascade = CascadeType.ALL)
    private List<BeanTransaction> transactionList = new ArrayList<>();
}
