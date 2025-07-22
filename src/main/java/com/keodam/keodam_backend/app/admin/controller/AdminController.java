package com.keodam.keodam_backend.app.admin.controller;

import com.keodam.keodam_backend.app.admin.domain.CustomAdminDetails;
import com.keodam.keodam_backend.app.admin.dto.AdminLoginDto;
import com.keodam.keodam_backend.app.admin.dto.AdminLoginResponseDto;
import com.keodam.keodam_backend.app.admin.dto.AdminPasswordUpdateDto;
import com.keodam.keodam_backend.app.admin.dto.AdminRegisterDto;
import com.keodam.keodam_backend.app.admin.service.AdminService;
import com.keodam.keodam_backend.global.ApiResponse;
import com.keodam.keodam_backend.global.code.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@Tag(name = "Admin", description = "Admin Management API")
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/register")
    @Operation(summary = "관리자 회원가입 신청", description = "미승인 상태의 관리자 계정 생성")
    public ResponseEntity<ApiResponse<String>> register(@RequestBody AdminRegisterDto dto) {
        adminService.register(dto);
        return ResponseEntity.status(SuccessStatus._OK.getHttpStatus())
                .body(ApiResponse.of(SuccessStatus._OK, "관리자 가입 요청이 완료되었습니다."));
    }

    @PostMapping("/login")
    @Operation(summary = "관리자 로그인", description = "승인된 관리자만 로그인 가능")
    public ResponseEntity<ApiResponse<AdminLoginResponseDto>> login(@RequestBody AdminLoginDto dto) {
        AdminLoginResponseDto admin = adminService.login(dto);
        return ResponseEntity.ok(ApiResponse.of(SuccessStatus._OK, admin));
    }

    @PatchMapping("/update-password")
    @Operation(summary = "비밀번호 변경", description = "본인 계정 비밀번호 변경")
    public ResponseEntity<ApiResponse<String>> updatePassword(
            @AuthenticationPrincipal CustomAdminDetails adminDetails,
            @RequestBody AdminPasswordUpdateDto dto) {
        adminService.updatePassword(adminDetails.getAdmin(), dto.currentPassword(), dto.newPassword());
        return ResponseEntity.ok(ApiResponse.of(SuccessStatus._OK, "비밀번호가 변경되었습니다."));
    }
}
