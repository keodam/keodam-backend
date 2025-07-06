package com.keodam.keodam_backend.community.repository;

import com.keodam.keodam_backend.community.domain.CommunityImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommunityImageRepository extends JpaRepository<CommunityImage, Long> {
    List<CommunityImage> findByCommunityId(Long communityId);
}
