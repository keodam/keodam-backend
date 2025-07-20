package com.keodam.keodam_backend.app.user.coffeeChatProfile.repository;

import com.keodam.keodam_backend.app.user.coffeeChatProfile.domain.Mentee;
import com.keodam.keodam_backend.app.user.coffeeChatProfile.domain.MenteeHashtag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenteeHashtagRepository extends JpaRepository<MenteeHashtag, Long> {
    void deleteByMentee(Mentee mentee);
    List<MenteeHashtag> findAllByMentee(Mentee mentee);
}
