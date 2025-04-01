package com.keodam.keodam_backend.app.user.controller;

import com.keodam.keodam_backend.app.user.domain.User;
import com.keodam.keodam_backend.app.user.dto.NicknameRequestDto;
import com.keodam.keodam_backend.app.user.dto.RoleRequestDto;
import com.keodam.keodam_backend.app.user.dto.SignupStatusResponseDto;
import com.keodam.keodam_backend.app.user.dto.UserResponseDto;
import com.keodam.keodam_backend.app.user.service.UserService;
import com.keodam.keodam_backend.exception.GeneralException;
import com.keodam.keodam_backend.global.ApiResponse;
import com.keodam.keodam_backend.global.code.status.ErrorStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "User API")
public class UserController {

    private final UserService userService;

    @PatchMapping("/nickname")
    @Operation(summary = "닉네임 설정 및 수정", description = "닉네임 설정 및 수정 API")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateNickname(
            @RequestBody NicknameRequestDto request,
            @AuthenticationPrincipal OAuth2User oAuth2User) {
        String oauthId = oAuth2User.getName();
        String email = oAuth2User.getAttribute("email");

        User user = userService.findByOAuthId(oauthId)
                .orElseGet(() -> userService.createUser(oauthId, email));

        UserResponseDto updatedUser = userService.updateNickname(user, request.nickname());

        return ResponseEntity.ok(ApiResponse.onSuccess(updatedUser));
    }

    @GetMapping("/nickname/check")
    @Operation(summary = "닉네임 중복 확인", description = "닉네임 검증 API")
    public ResponseEntity<ApiResponse<String>> checkNickname(@RequestParam String nickname) {
        userService.validateNickname(nickname);
        return ResponseEntity.ok(ApiResponse.onSuccess("사용 가능한 닉네임입니다."));
    }

    @PatchMapping("/role")
    @Operation(summary = "역할 설정 및 수정", description = "역할 설정 및 수정 API")
    public ResponseEntity<ApiResponse<UserResponseDto>> selectRole(
            @RequestBody RoleRequestDto request,
            @AuthenticationPrincipal OAuth2User oAuth2User) {
        String oauthId = oAuth2User.getName();
        User user = userService.findByOAuthId(oauthId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        UserResponseDto updatedUser = userService.updateRole(user, request.roleType());

        return ResponseEntity.ok(ApiResponse.onSuccess(updatedUser));
    }

    @GetMapping("/status")
    @Operation(summary = "회원가입 상태 확인", description = "회원가입 상태 확인 API")
    public ResponseEntity<ApiResponse<SignupStatusResponseDto>> checkSignupStatus(
            @AuthenticationPrincipal  OAuth2User oAuth2User
    ) {
        String oauthId = oAuth2User.getName();
        User user = userService.findByOAuthId(oauthId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        SignupStatusResponseDto response = userService.checkSignupStatus(user);

        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }
}
