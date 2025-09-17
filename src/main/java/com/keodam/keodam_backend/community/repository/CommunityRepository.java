package com.keodam.keodam_backend.community.repository;

import com.keodam.keodam_backend.app.user.domain.User;
import com.keodam.keodam_backend.community.domain.Community;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommunityRepository extends JpaRepository<Community, Long> {
    void deleteByUser(User user);
    List<Community> findAllByUser(User user);
}
