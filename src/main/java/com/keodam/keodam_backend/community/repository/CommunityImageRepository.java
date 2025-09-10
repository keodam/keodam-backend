package com.keodam.keodam_backend.community.repository;

import com.keodam.keodam_backend.community.domain.CommunityImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommunityImageRepository extends JpaRepository<CommunityImage, Long> {
    List<CommunityImage> findByCommunityId(Long communityId);

    @Modifying
    @Query("DELETE FROM CommunityImage ci WHERE ci.community.id = :communityId")
    void deleteByCommunityId(@Param("communityId") Long communityId);
}
