package com.keodam.keodam_backend.mypage.payment.domain;

import com.keodam.keodam_backend.app.user.domain.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "payment")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "order_name", nullable = false)
    private String orderName;

    @Column(name = "receipt_id", unique = true)
    private String receiptId;

    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "bean_amount", nullable = false)
    private int beanAmount;

    @Column(name = "method", nullable = false)
    private String method; //지불수단

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; //결제한 유저

    @CreatedDate
    @Column(name = "requested_at")
    private LocalDateTime requestedAt;

    @Builder
    public Payment(String orderName, String receiptId, User user, int price, String method, int beanAmount, PaymentStatus paymentStatus) {
        this.orderName = orderName;
        this.receiptId = receiptId;
        this.user = user;
        this.price = price;
        this.method = method;
        this.beanAmount = beanAmount;
        this.paymentStatus = PaymentStatus.APPROVED;
    }
}
