package com.keodam.keodam_backend.community.service;

import com.keodam.keodam_backend.app.domain.User;
import com.keodam.keodam_backend.app.repository.UserRepository;
import com.keodam.keodam_backend.community.domain.Community;
import com.keodam.keodam_backend.community.domain.CommunityComment;
import com.keodam.keodam_backend.community.dto.request.CommunityCommentRequestDto;
import com.keodam.keodam_backend.community.dto.response.CommunityCommentResponseDto;
import com.keodam.keodam_backend.community.dto.response.CommunityCommentResponseListDto;
import com.keodam.keodam_backend.community.repository.CommunityCommentRepository;
import com.keodam.keodam_backend.community.repository.CommunityRepository;
import com.keodam.keodam_backend.exception.GeneralException;
import com.keodam.keodam_backend.global.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityCommentService {

    private final CommunityCommentRepository communityCommentRepository;
    private final UserRepository userRepository;
    private final CommunityRepository communityRepository;

    @Transactional
    public CommunityCommentResponseDto createComment(String email, Long communityId, CommunityCommentRequestDto commentRequestDto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.COMMUNITY_NOT_FOUND));

        CommunityComment parent = null;
        if (commentRequestDto.getParentId() != null) {
            parent = communityCommentRepository.findById(commentRequestDto.getParentId())
                    .orElseThrow(() -> new GeneralException(ErrorStatus.COMMUNITY_COMMENT_NOT_FOUND));
        }

        CommunityComment comment = communityCommentRepository.save(CommunityComment.builder()
                .content(commentRequestDto.getContent())
                .user(user)
                .community(community)
                .parent(parent)
                .build());

        return CommunityCommentResponseDto.from(comment);
    }

    @Transactional(readOnly = true)
    public CommunityCommentResponseListDto getComments(Long communityId) {
        List<CommunityComment> rootComments = communityCommentRepository.findByCommunityIdAndParentIsNull(communityId);

        List<CommunityCommentResponseDto> dtoList = rootComments.stream()
                .map(CommunityCommentResponseDto::from)
                .collect(Collectors.toList());

        return CommunityCommentResponseListDto.builder()
                .comments(dtoList)
                .build();
    }

    @Transactional
    public CommunityCommentResponseDto updateComment(String email, Long commentId, CommunityCommentRequestDto commentRequestDto) {
        CommunityComment communityComment = communityCommentRepository.findById(commentId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.COMMUNITY_COMMENT_NOT_FOUND));

        if (communityComment.getUser().getEmail().equals(email)) {
            throw new GeneralException(ErrorStatus.UNAUTHORIZED_COMMENT_MODIFICATION);
        }

        communityComment.updateComment(commentRequestDto.getContent());
        CommunityComment saveComment = communityCommentRepository.save(communityComment);

        return CommunityCommentResponseDto.from(saveComment);
    }

    @Transactional
    public void deleteComment(String email, Long commentId) {
        CommunityComment communityComment = communityCommentRepository.findById(commentId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.COMMUNITY_COMMENT_NOT_FOUND));

        if (!communityComment.getUser().getEmail().equals(email)) {
            throw new GeneralException(ErrorStatus.UNAUTHORIZED_COMMENT_MODIFICATION);
        }
        communityCommentRepository.delete(communityComment);
    }
}
