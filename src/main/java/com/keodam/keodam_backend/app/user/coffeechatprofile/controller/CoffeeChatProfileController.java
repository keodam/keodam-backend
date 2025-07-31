package com.keodam.keodam_backend.app.user.coffeechatprofile.controller;

import com.keodam.keodam_backend.app.user.coffeechatprofile.dto.request.MenteeRequestDto;
import com.keodam.keodam_backend.app.user.coffeechatprofile.dto.response.MenteeResponseDto;
import com.keodam.keodam_backend.app.user.coffeechatprofile.dto.request.MentorRequestDto;
import com.keodam.keodam_backend.app.user.coffeechatprofile.dto.response.MentorResponseDto;
import com.keodam.keodam_backend.app.user.coffeechatprofile.service.MenteeProfileService;
import com.keodam.keodam_backend.app.user.coffeechatprofile.service.MentorProfileService;
import com.keodam.keodam_backend.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Tag(name = "User", description = "User API")
public class CoffeeChatProfileController {

    private final MenteeProfileService menteeProfileService;
    private final MentorProfileService mentorProfileService;

    @PatchMapping("/mentee")
    @Operation(summary = "커피챗 멘티 프로필 등록 및 수정", description = "멘티 프로필 등록 및 수정 API", security = @SecurityRequirement(name = "Authorization"))
    public ApiResponse<MenteeResponseDto> updateMentee(Authentication authentication,
                                                       @RequestBody MenteeRequestDto menteeRequestDto) {
        String email = authentication.getName();
        return ApiResponse.onSuccess(menteeProfileService.updateMentee(email, menteeRequestDto));
    }

    @PatchMapping("/mentor")
    @Operation(summary = "커피챗 멘토 프로필 등록 및 수정", description = "멘토 프로필 등록 및 수정 API", security = @SecurityRequirement(name = "Authorization"))
    public ApiResponse<MentorResponseDto> updateMentor(Authentication authentication,
                                                       @RequestBody MentorRequestDto mentorRequestDto) {
        String email = authentication.getName();
        return ApiResponse.onSuccess(mentorProfileService.updateMentor(email, mentorRequestDto));
    }

    @GetMapping("/mentee/{menteeId}")
    @Operation(summary = "커피챗 멘티 프로필 조회", description = "멘티 프로필 조회", security = @SecurityRequirement(name = "Authorization"))
    public ApiResponse<MenteeResponseDto> getMentee(@PathVariable Long menteeId) {
        return ApiResponse.onSuccess(menteeProfileService.getMentee(menteeId));
    }

    @GetMapping("/mentor/{mentorId}")
    @Operation(summary = "커피챗 멘토 프로필 조회", description = "멘토 프로필 조회", security = @SecurityRequirement(name = "Authorization"))
    public ApiResponse<MentorResponseDto> getMentor(@PathVariable Long mentorId) {
        return ApiResponse.onSuccess(mentorProfileService.getMentor(mentorId));
    }
}
