package com.keodam.keodam_backend.mypage.payment.domain;

import jakarta.persistence.*;
import com.keodam.keodam_backend.app.domain.User;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Entity
@Table(name = "bean_transaction")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
public class BeanTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; //누가 원두를 사용했는지, 충전했는지, 환불받았는지, 환급받았는 지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = true)
    private Payment payment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payout_id", nullable = true)
    private Payout payout;

    @Enumerated(EnumType.STRING)
    private BeanTransactionType type;

    @Column(name = "bean_amount", nullable = false)
    private int beanAmount;

    @CreatedDate
    @Column(name = "requested_at")
    private LocalDateTime requestedAt;

    @Builder
    public BeanTransaction(User user, Payment payment, Payout payout, BeanTransactionType type, int beanAmount) {
        this.user = user;
        this.payment = payment;
        this.payout = payout;
        this.type = type;
        this.beanAmount = beanAmount;
    }
}
