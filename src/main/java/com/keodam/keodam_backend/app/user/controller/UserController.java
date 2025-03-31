package com.keodam.keodam_backend.app.user.controller;

import com.keodam.keodam_backend.app.user.domain.User;
import com.keodam.keodam_backend.app.user.dto.req.NicknameRequestDto;
import com.keodam.keodam_backend.app.user.dto.req.RoleRequestDto;
import com.keodam.keodam_backend.app.user.dto.res.SignupStatusResponseDto;
import com.keodam.keodam_backend.app.user.dto.res.UserResponseDto;
import com.keodam.keodam_backend.app.user.service.UserService;
import com.keodam.keodam_backend.global.ApiResponse;
import com.keodam.keodam_backend.global.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PatchMapping("/nickname")
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
    public ResponseEntity<ApiResponse<String>> checkNickname(@RequestParam String nickname) {
        if (nickname == null || nickname.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.onFailure(
                    ErrorStatus.NICKNAME_NOT_EXIST.getCode(),
                    ErrorStatus.NICKNAME_NOT_EXIST.getMessage(),
                    null
            ));
        }

        boolean isAvailable = userService.isNicknameAvailable(nickname);
        if (isAvailable) {
            return ResponseEntity.ok(ApiResponse.onSuccess("사용 가능한 닉네임입니다."));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.onFailure(
                    ErrorStatus.USER_NOT_FOUND.getCode(),
                    "이미 사용 중인 닉네임입니다.",
                    null
            ));
        }

    }

    @PatchMapping("/role")
    public ResponseEntity<ApiResponse<UserResponseDto>> selectRole(
            @RequestBody RoleRequestDto request,
            @AuthenticationPrincipal OAuth2User oAuth2User) {


        String oauthId = oAuth2User.getName();
        User user = userService.findByOAuthId(oauthId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        UserResponseDto updatedUser = userService.updateRole(user, request.roleType());

        return ResponseEntity.ok(ApiResponse.onSuccess(updatedUser));
    }

    // 회원가입 진행 상태 확인
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<SignupStatusResponseDto>> checkSignupStatus(
            @AuthenticationPrincipal  OAuth2User oAuth2User
    ) {
        String oauthId = oAuth2User.getName();  // OAuth2 ID 가져오기
        User user = userService.findByOAuthId(oauthId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        SignupStatusResponseDto response = userService.checkSignupStatus(user);

        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }


}
