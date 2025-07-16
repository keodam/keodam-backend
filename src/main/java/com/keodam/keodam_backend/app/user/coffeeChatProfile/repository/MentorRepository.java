package com.keodam.keodam_backend.app.user.coffeeChatProfile.repository;

import com.keodam.keodam_backend.app.user.coffeeChatProfile.domain.Mentor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MentorRepository extends JpaRepository<Mentor, Long> {
}
