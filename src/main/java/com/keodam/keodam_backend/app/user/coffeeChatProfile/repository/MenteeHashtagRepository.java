package com.keodam.keodam_backend.app.user.coffeeChatProfile.repository;

import com.keodam.keodam_backend.app.user.coffeeChatProfile.domain.MenteeHashtag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenteeHashtagRepository extends JpaRepository<MenteeHashtag, Long> {
}
