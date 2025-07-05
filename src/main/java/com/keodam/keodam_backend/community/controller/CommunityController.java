package com.keodam.keodam_backend.community.controller;

import com.keodam.keodam_backend.community.dto.request.CommunityRequestDto;
import com.keodam.keodam_backend.community.dto.response.CommunityResponseDto;
import com.keodam.keodam_backend.community.service.CommunityService;
import com.keodam.keodam_backend.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/community")
public class CommunityController {

    private final CommunityService communityService;

    @PostMapping(value = "/create", consumes = "multipart/form-data")
    @Operation(summary = "커뮤니티 게시물 작성", description = "커뮤니티 게시물 작성 API", security = @SecurityRequirement(name = "Authorization"))
    public ApiResponse<CommunityResponseDto> createCommunityPost(Authentication authentication,
                                                      @RequestPart CommunityRequestDto communityRequestDto,
                                                      @RequestPart(name = "ImageFile", required = false) List<MultipartFile> imgs
    ) {
        String email = authentication.getName();
        return ApiResponse.onSuccess(communityService.createCommunityPost(email, communityRequestDto, imgs));
    }



}
