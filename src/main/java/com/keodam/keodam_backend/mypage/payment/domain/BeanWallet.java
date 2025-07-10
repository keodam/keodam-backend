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

    private int totalBeans;

    @Column(name = "bean_amount_referral", nullable = false)
    private int beanAmountReferral = 0;

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

    public void increaseReferral(int amount) {
        this.beanAmountReferral += amount;
    }

    public boolean canUseReferralBeans(int amount){
        return this.beanAmountReferral >= amount;
    }

    public void decreaseReferral(int amount) {
        this.beanAmountReferral -= amount;
    }

    @Builder
    public BeanWallet(int totalBeans, User user) {
        this.totalBeans = totalBeans;
        this.user = user;
    }
}
