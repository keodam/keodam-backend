package com.keodam.keodam_backend.term.repository;

import com.keodam.keodam_backend.term.domain.Term;
import com.keodam.keodam_backend.term.domain.TermType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface TermRepository extends JpaRepository<Term, Long> {

    Optional<Term> findTopByTypeOrderByVersionDesc(TermType type);

    Optional<Term> findByTypeAndVersion(TermType type, Integer version);

    @Query("SELECT COALESCE(MAX(t.version), 0) FROM Term t WHERE t.type = :type")
    Integer findMaxVersionByType(@Param("type") TermType type);
}
