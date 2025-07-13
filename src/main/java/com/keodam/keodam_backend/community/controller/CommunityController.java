package com.keodam.keodam_backend.community.controller;

import com.keodam.keodam_backend.community.dto.request.CommunityRequestDto;
import com.keodam.keodam_backend.community.dto.response.CommunityResponseDto;
import com.keodam.keodam_backend.community.service.CommunityService;
import com.keodam.keodam_backend.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/community")
@Tag(name = "User COMMUNITY", description = "COMMUNITY API")
public class CommunityController {

    private final CommunityService communityService;

    @PostMapping(value = "/create", consumes = "multipart/form-data")
    @Operation(summary = "커뮤니티 게시물 작성", description = "커뮤니티 게시물 작성 API", security = @SecurityRequirement(name = "Authorization"))
    public ApiResponse<CommunityResponseDto> createCommunityPost(Authentication authentication,
                                                                 @RequestPart CommunityRequestDto communityRequestDto,
                                                                 @RequestPart(name = "ImageFile", required = false) List<MultipartFile> imgs) {
        String email = authentication.getName();
        return ApiResponse.onSuccess(communityService.createCommunityPost(email, communityRequestDto, imgs));
    }

    @GetMapping("/{communityId}")
    @Operation(summary = "커뮤니티 게시물 상세 조회", description = "커뮤니티 게시물 상세 조회 API", security = @SecurityRequirement(name = "Authorization"))
    public ApiResponse<CommunityResponseDto> getCommunityPost(@PathVariable Long communityId) {
        return ApiResponse.onSuccess(communityService.getCommunityPost(communityId));
    }

    @PatchMapping(value = "/{communityId}", consumes = "multipart/form-data")
    @Operation(summary = "커뮤니티 게시물 수정", description = "커뮤니티 게시물 수정 API", security = @SecurityRequirement(name = "Authorization"))
    public ApiResponse<CommunityResponseDto> updateCommunityPost(Authentication authentication,
                                                                 @PathVariable Long communityId,
                                                                 @RequestPart CommunityRequestDto communityRequestDto,
                                                                 @RequestPart(name = "ImageFile", required = false) List<MultipartFile> imgs) {
        String email = authentication.getName();
        return ApiResponse.onSuccess(communityService.updateCommunityPost(email, communityId, communityRequestDto, imgs));
    }

    @DeleteMapping("/{communityId}")
    @Operation(summary = "커뮤니티 게시물 삭제", description = "커뮤니티 게시물 삭제 API", security = @SecurityRequirement(name = "Authorization"))
    public ApiResponse deleteCommunityPost(Authentication authentication,
                                           @PathVariable Long communityId) {
        String email = authentication.getName();
        communityService.deleteCommunityPost(email, communityId);
        return ApiResponse.onSuccess("Community post deleted successfully");
    }
}
