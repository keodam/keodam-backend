package com.keodam.keodam_backend.app.user.coffeeChatProfile.repository;

import com.keodam.keodam_backend.app.user.coffeeChatProfile.domain.Mentee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenteeRepository extends JpaRepository<Mentee, Long> {
}
