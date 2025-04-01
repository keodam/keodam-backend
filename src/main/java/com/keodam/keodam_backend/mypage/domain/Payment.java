package com.keodam.keodam_backend.mypage.domain;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@EntityListeners(AuditingEntityListener.class)
public class Payment {

    @Id
    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Column(name = "order_name", nullable = false)
    private String orderName;

    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "method",nullable = false)
    private String method; //지불수단

    @CreatedDate
    private LocalDateTime requestedAt;
}
