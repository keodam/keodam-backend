package com.keodam.keodam_backend.app.user.coffeeChatProfile.controller;

import com.keodam.keodam_backend.app.user.coffeeChatProfile.dto.MenteeRequestDto;
import com.keodam.keodam_backend.app.user.coffeeChatProfile.dto.MenteeResponseDto;
import com.keodam.keodam_backend.app.user.coffeeChatProfile.service.MenteeProfileService;
import com.keodam.keodam_backend.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Tag(name = "User", description = "User API")
public class CoffeeChatProfileController {

    private final MenteeProfileService menteeProfileService;

    @PatchMapping("/mentee")
    @Operation(summary = "커피챗 멘티 프로필 등록", description = "멘티 프로필 등록 API", security = @SecurityRequirement(name = "Authorization"))
    public ApiResponse<MenteeResponseDto> updateMentee(Authentication authentication,
                                                       @RequestBody MenteeRequestDto menteeRequestDto) {
        String email = authentication.getName();
        return ApiResponse.onSuccess(menteeProfileService.updateMentee(email, menteeRequestDto));
    }
}
