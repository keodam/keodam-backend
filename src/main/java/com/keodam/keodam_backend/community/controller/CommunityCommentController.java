package com.keodam.keodam_backend.community.controller;

import com.keodam.keodam_backend.community.dto.request.CommunityCommentRequestDto;
import com.keodam.keodam_backend.community.dto.response.CommunityCommentResponseDto;
import com.keodam.keodam_backend.community.dto.response.CommunityCommentResponseListDto;
import com.keodam.keodam_backend.community.service.CommunityCommentService;
import com.keodam.keodam_backend.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/community/comment")
@Tag(name = "COMMENT", description = "COMMUNITY COMMENT API")
public class CommunityCommentController {

    private final CommunityCommentService communityCommentService;

    @PostMapping("/{communityId}")
    @Operation(summary = "커뮤니티 게시물 댓글 작성", description = "커뮤니티 게시물 댓글 작성 API", security = @SecurityRequirement(name = "Authorization"))
    public ApiResponse<CommunityCommentResponseDto> createComment(Authentication authentication,
                                                                  @PathVariable Long communityId,
                                                                  @RequestBody CommunityCommentRequestDto commentRequestDto) {
        String email = authentication.getName();
        return ApiResponse.onSuccess(communityCommentService.createComment(email, communityId, commentRequestDto));
    }

    @GetMapping("/{communityId}")
    @Operation(summary = "커뮤니티 게시물에 따른 댓글 목록 조회", description = "커뮤니티 게시물 댓글 목록 조회 API", security = @SecurityRequirement(name = "Authorization"))
    public ApiResponse<CommunityCommentResponseListDto> getComment(@PathVariable Long communityId) {
        return ApiResponse.onSuccess(communityCommentService.getComments(communityId));
    }

    @PatchMapping("/{commentId}")
    @Operation(summary = "커뮤니티 게시물 댓글 수정", description = "커뮤니티 게시물 댓글 수정 API", security = @SecurityRequirement(name = "Authorization"))
    public ApiResponse<CommunityCommentResponseDto> updateComment(Authentication authentication,
                                                                  @PathVariable Long commentId,
                                                                  @RequestBody CommunityCommentRequestDto commentRequestDto) {
        String email = authentication.getName();
        return ApiResponse.onSuccess(communityCommentService.updateComment(email, commentId, commentRequestDto));
    }

    @DeleteMapping("/{commentId}")
    @Operation(summary = "커뮤니티 게시물 댓글 삭제", description = "커뮤니티 게시물 댓글 삭제 API", security = @SecurityRequirement(name = "Authorization"))
    public ApiResponse deleteComment(Authentication authentication,
                                     @PathVariable Long commentId) {
        String email = authentication.getName();
        communityCommentService.deleteComment(email, commentId);
        return ApiResponse.onSuccess("comment deleted successfully");
    }
}
