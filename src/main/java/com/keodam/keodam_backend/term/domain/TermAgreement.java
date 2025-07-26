package com.keodam.keodam_backend.term.domain;

import com.keodam.keodam_backend.app.user.domain.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Entity
@Getter
@Builder
@Table(name = "terms_agreements")
@IdClass(TermAgreementId.class)
@AllArgsConstructor
@RequiredArgsConstructor
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
