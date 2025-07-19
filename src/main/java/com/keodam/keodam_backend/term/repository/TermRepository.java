package com.keodam.keodam_backend.term.repository;

import com.keodam.keodam_backend.term.domain.Term;
import com.keodam.keodam_backend.term.domain.TermType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TermRepository extends JpaRepository<Term, Long> {

    Optional<Term> findTopByTermTypeOrderByVersionDesc(TermType termType);

    Optional<Term> findByTermTypeAndVersion(TermType termType, Integer version);
}
