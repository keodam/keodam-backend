package com.keodam.keodam_backend.app.user.referral.repository;

import com.keodam.keodam_backend.app.user.domain.User;
import com.keodam.keodam_backend.app.user.referral.domain.Referral;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReferralRepository extends JpaRepository<Referral, Long> {
    Optional<Referral> findBySponsor(User referrer);
}
