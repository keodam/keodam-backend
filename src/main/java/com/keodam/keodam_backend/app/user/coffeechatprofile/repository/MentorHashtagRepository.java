package com.keodam.keodam_backend.app.user.coffeechatprofile.repository;

import com.keodam.keodam_backend.app.user.coffeechatprofile.domain.Mentor;
import com.keodam.keodam_backend.app.user.coffeechatprofile.domain.MentorHashtag;
import com.keodam.keodam_backend.app.user.coffeechatprofile.domain.MentorHashtagType;
import com.keodam.keodam_backend.app.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MentorHashtagRepository extends JpaRepository<MentorHashtag, Long> {
    void deleteByMentorAndHashtagType(Mentor mentor, MentorHashtagType mentorHashtagType);
    List<MentorHashtag> findAllByMentorAndHashtagType(Mentor mentor, MentorHashtagType type);
    void deleteByMentor(Mentor mentor);
}
