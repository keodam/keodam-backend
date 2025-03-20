package com.keodam.keodam_backend.app.user.controller;

import com.keodam.keodam_backend.app.user.domain.User;
import com.keodam.keodam_backend.app.user.dto.req.NicknameRequestDto;
import com.keodam.keodam_backend.app.user.dto.req.RoleRequestDto;
import com.keodam.keodam_backend.app.user.dto.res.SignupStatusResponseDto;
import com.keodam.keodam_backend.app.user.dto.res.UserResponseDto;
import com.keodam.keodam_backend.app.user.service.UserService;
import com.keodam.keodam_backend.global.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PatchMapping("/nickname")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateNickname(
            @RequestBody NicknameRequestDto request,
            @AuthenticationPrincipal CustomOAuth2User userDetails) {
        User user = userDetails.getUser();
        UserResponseDto updatedUser = userService.updateNickname(user, request.nickname());

        return ResponseEntity.ok(ApiResponse.onSuccess(updatedUser));
    }


    @PatchMapping("/role")
    public ResponseEntity<ApiResponse<UserResponseDto>> selectRole(
            @RequestBody RoleRequestDto request,
            @AuthenticationPrincipal CustomOAuth2User userDetails) {

        User user = userDetails.getUser();
        UserResponseDto updatedUser = userService.updateRole(user, request.roleType());

        return ResponseEntity.ok(ApiResponse.onSuccess(updatedUser));
    }

    // 회원가입 진행 상태 확인
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<SignupStatusResponseDto>> checkSignupStatus(
            @AuthenticationPrincipal CustomOAuth2User userDetails
    ) {
        User user = userDetails.getUser();
        SignupStatusResponseDto response = userService.checkSignupStatus(user);

        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }


}
