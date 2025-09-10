package com.keodam.keodam_backend.term.repository;

import com.keodam.keodam_backend.app.user.domain.User;
import com.keodam.keodam_backend.term.domain.Term;
import com.keodam.keodam_backend.term.domain.TermAgreement;
import com.keodam.keodam_backend.term.domain.TermAgreementId;
import com.keodam.keodam_backend.term.domain.TermType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TermAgreementRepository extends JpaRepository<TermAgreement, TermAgreementId> {

    Optional<TermAgreement> findByUserAndTerm(User user, Term term);

    boolean existsByUserAndTerm_Type(User user, TermType type);

    void deleteByUser(User user);
}
