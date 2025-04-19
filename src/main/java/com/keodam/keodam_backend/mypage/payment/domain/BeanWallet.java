package com.keodam.keodam_backend.mypage.payment.domain;

import com.keodam.keodam_backend.app.domain.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "bean_wallet")
@NoArgsConstructor
public class BeanWallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int totalBeans; //현재 보유 원두

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL)
    private List<BeanTransaction> transactions = new ArrayList<>();

    public void increase(int amount) {
        this.totalBeans += amount;
    }

    public void decrease(int amount) {
        this.totalBeans -= amount;
    }

    @Builder
    public BeanWallet(int totalBeans, User user) {
        this.totalBeans = totalBeans;
        this.user = user;
    }
}
