package com.keodam.keodam_backend.community.repository;

import com.keodam.keodam_backend.community.domain.CommunityComment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommunityCommentRepository extends JpaRepository<CommunityComment, Long> {
    List<CommunityComment> findByCommunityIdAndParentIsNull(Long communityId);
}
