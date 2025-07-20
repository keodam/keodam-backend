package com.keodam.keodam_backend.app.user.coffeeChatProfile.repository;

import com.keodam.keodam_backend.app.user.coffeeChatProfile.domain.Mentor;
import com.keodam.keodam_backend.app.user.coffeeChatProfile.domain.MentorHashtag;
import com.keodam.keodam_backend.app.user.coffeeChatProfile.domain.MentorHashtagType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MentorHashtagRepository extends JpaRepository<MentorHashtag, Long> {
    void deleteByMentorAndHashtagType(Mentor mentor, MentorHashtagType mentorHashtagType);
    List<MentorHashtag> findAllByMentorAndHashtagType(Mentor mentor, MentorHashtagType type);
}
