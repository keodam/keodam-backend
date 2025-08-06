package com.keodam.keodam_backend.app.user.coffeechatprofile.controller;

import com.keodam.keodam_backend.app.user.coffeechatprofile.dto.request.PreferenceRequestDto;
import com.keodam.keodam_backend.app.user.coffeechatprofile.service.ProfilePreferenceService;
import com.keodam.keodam_backend.global.ApiResponse;
import com.keodam.keodam_backend.global.code.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User", description = "User API")
@RequiredArgsConstructor
public class ProfilePreferenceController {

    private final ProfilePreferenceService profilePreferenceService;

    @Operation(summary = "선호 만남 설정 업데이트", description = "사용자의 멘토/멘티중 설정한 프로필의 선호 요일 및 장소 정보를 업데이트합니다.", security = @SecurityRequirement(name = "Authorization"))
    @PostMapping("/preferences")
    public ResponseEntity<ApiResponse<String>> updatePreferences(
            Authentication authentication,
            @Valid @RequestBody PreferenceRequestDto request) {
        String email = authentication.getName();
        profilePreferenceService.updatePreferences(email, request);
        return ResponseEntity.ok(ApiResponse.of(SuccessStatus._OK, "선호 만남 설정이 업데이트되었습니다."));
    }
}
