package com.keodam.keodam_backend.app.user.coffeechatprofile.repository;

import com.keodam.keodam_backend.app.user.coffeechatprofile.domain.Mentee;
import com.keodam.keodam_backend.app.user.coffeechatprofile.domain.MenteeHashtag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenteeHashtagRepository extends JpaRepository<MenteeHashtag, Long> {
    void deleteByMentee(Mentee mentee);
    List<MenteeHashtag> findAllByMentee(Mentee mentee);
}
