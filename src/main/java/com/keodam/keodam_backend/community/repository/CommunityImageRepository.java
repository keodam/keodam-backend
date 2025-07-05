package com.keodam.keodam_backend.community.repository;

import com.keodam.keodam_backend.community.domain.CommunityImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityImageRepository extends JpaRepository<CommunityImage, Long> {
}
