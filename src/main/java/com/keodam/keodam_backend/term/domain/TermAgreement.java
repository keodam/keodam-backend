package com.keodam.keodam_backend.term.domain;

import com.keodam.keodam_backend.app.domain.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;


@Entity
@Getter
@Builder
@Table(name = "terms_agreements")
@IdClass(TermAgreementId.class)
public class TermAgreement {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "term_id")
    private Term term;

    @Column(name = "agreed_at", nullable = false)
    private LocalDateTime agreedAt;
}
