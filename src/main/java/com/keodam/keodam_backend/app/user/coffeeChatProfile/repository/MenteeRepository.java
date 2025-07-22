package com.keodam.keodam_backend.app.user.coffeeChatProfile.repository;

import com.keodam.keodam_backend.app.user.domain.User;
import com.keodam.keodam_backend.app.user.coffeeChatProfile.domain.Mentee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MenteeRepository extends JpaRepository<Mentee, Long> {
    Optional<Mentee> findByUser(User user);
}
