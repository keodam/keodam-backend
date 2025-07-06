package com.keodam.keodam_backend.community.repository;

import com.keodam.keodam_backend.community.domain.Community;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityRepository extends JpaRepository<Community, Long> {
}
