package com.keodam.keodam_backend.term.repository;

import com.keodam.keodam_backend.app.domain.User;
import com.keodam.keodam_backend.term.domain.Term;
import com.keodam.keodam_backend.term.domain.TermAgreement;
import com.keodam.keodam_backend.term.domain.TermAgreementId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TermAgreementRepository extends JpaRepository<TermAgreement, TermAgreementId> {

    Optional<TermAgreement> findByUserAndTerm(User user, Term term);
}
