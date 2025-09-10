package com.keodam.keodam_backend.community.repository;

import com.keodam.keodam_backend.app.user.domain.User;
import com.keodam.keodam_backend.community.domain.Community;
import com.keodam.keodam_backend.community.domain.CommunityComment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CommunityCommentRepository extends JpaRepository<CommunityComment, Long> {
    List<CommunityComment> findByCommunityIdAndParentIsNull(Long communityId);
    Optional<CommunityComment> findByUser(User user);
    void deleteByUser(User user);
    void deleteByCommunity(Community community);
}
