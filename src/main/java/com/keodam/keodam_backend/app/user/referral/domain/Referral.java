package com.keodam.keodam_backend.app.user.referral.domain;

import com.keodam.keodam_backend.app.domain.User;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "referral")
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Referral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "sponsor_id")
    private User sponsor;

    @ManyToOne
    @JoinColumn(name = "referee_id")
    private User referee;

    @CreatedDate
    private LocalDateTime createdAt;

    @Builder
    public Referral(User sponsor, User referee) {
        this.sponsor = sponsor;
        this.referee = referee;
    }
}
