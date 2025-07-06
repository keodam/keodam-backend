package com.keodam.keodam_backend.app.user.controller;

import com.keodam.keodam_backend.app.domain.User;
import com.keodam.keodam_backend.app.user.dto.NicknameRequestDto;
import com.keodam.keodam_backend.app.user.dto.RoleRequestDto;
import com.keodam.keodam_backend.app.user.dto.UserResponseDto;
import com.keodam.keodam_backend.app.user.service.UserService;
import com.keodam.keodam_backend.exception.GeneralException;
import com.keodam.keodam_backend.global.ApiResponse;
import com.keodam.keodam_backend.global.code.status.ErrorStatus;
import com.keodam.keodam_backend.mypage.dto.request.FileRequestDto;
import com.keodam.keodam_backend.mypage.dto.response.FileResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "User API")
public class UserController {

    private final UserService userService;

    @PatchMapping("/nickname")
    @Operation(summary = "닉네임 설정 및 수정", description = "닉네임 설정 및 수정 API", security = @SecurityRequirement(name = "Authorization"))
    public ResponseEntity<ApiResponse<UserResponseDto>> updateNickname(
            @RequestBody NicknameRequestDto nicknameRequestDto,
            Authentication authentication) {
        String email = authentication.getName();

        User user = userService.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        UserResponseDto updatedUser = userService.updateNickname(user, nicknameRequestDto.nickname());

        return ResponseEntity.ok(ApiResponse.onSuccess(updatedUser));
    }

    @GetMapping("/nickname/check")
    @Operation(summary = "닉네임 중복 확인", description = "닉네임 검증 API", security = @SecurityRequirement(name = "Authorization"))
    public ResponseEntity<ApiResponse<String>> checkNickname(@RequestParam String nickname) {
        userService.validateNickname(nickname);
        return ResponseEntity.ok(ApiResponse.onSuccess("사용 가능한 닉네임입니다."));
    }

    @PatchMapping("/role")
    @Operation(summary = "역할 설정 및 수정", description = "역할 설정 및 수정 API", security = @SecurityRequirement(name = "Authorization"))
    public ResponseEntity<ApiResponse<UserResponseDto>> selectRole(
            @RequestBody RoleRequestDto roleRequestDto,
            Authentication authentication) {
        String email = authentication.getName();

        User user = userService.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        UserResponseDto updatedUser = userService.updateRole(user, roleRequestDto.roleType());

        return ResponseEntity.ok(ApiResponse.onSuccess(updatedUser));
    }

    @PatchMapping(value = "file", consumes = "multipart/form-data")
    @Operation(summary = "사용자 프로필 사진 등록 및 수정", description = "파일이미지로 프로필 등록 API", security = @SecurityRequirement(name = "Authorization"))
    public ResponseEntity<ApiResponse<UserResponseDto>> uploadProfileImage(Authentication authentication,
                                                             @RequestPart(name = "ImageFile", required = true) MultipartFile file) {
        String email = authentication.getName();
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        return ResponseEntity.ok(ApiResponse.onSuccess(userService.uploadProfileImage(user, file)));
    }
}
