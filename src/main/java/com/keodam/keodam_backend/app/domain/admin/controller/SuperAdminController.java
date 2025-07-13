package com.keodam.keodam_backend.app.domain.admin.controller;

import com.keodam.keodam_backend.app.domain.admin.domain.CustomAdminDetails;
import com.keodam.keodam_backend.app.domain.admin.dto.AdminResponseDto;
import com.keodam.keodam_backend.app.domain.admin.dto.SuperAdminPasswordResetDto;
import com.keodam.keodam_backend.app.domain.admin.dto.SuperAdminRequestDto;
import com.keodam.keodam_backend.app.domain.admin.service.AdminService;
import com.keodam.keodam_backend.global.ApiResponse;
import com.keodam.keodam_backend.global.code.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/super")
@Tag(name = "Admin_SUPER", description = "Super Admin 전용 API")
public class SuperAdminController {

    private final AdminService adminService;

    @PatchMapping("/approve")
    @Operation(summary = "관리자 승인", description = "계정 승인")
    public ResponseEntity<ApiResponse<String>> approve(
            @AuthenticationPrincipal CustomAdminDetails superAdmin,
            @RequestBody SuperAdminRequestDto dto) {
        adminService.approveAdmin(dto, superAdmin.getAdmin());
        return ResponseEntity.ok(ApiResponse.of(SuccessStatus._OK, "승인이 완료되었습니다."));
    }

    @PostMapping("/all")
    @Operation(summary = "전체 관리자 조회", description = "관리자 목록 조회")
    public ResponseEntity<ApiResponse<List<AdminResponseDto>>> getAllAdmins(@AuthenticationPrincipal CustomAdminDetails superAdmin) {
        List<AdminResponseDto> admins = adminService.getAllAdmins(superAdmin.getAdmin());
        return ResponseEntity.ok(ApiResponse.of(SuccessStatus._OK, admins));
    }

    @PatchMapping("/reject")
    @Operation(summary = "관리자 권한 회수", description = "권한 제거")
    public ResponseEntity<ApiResponse<String>> reject(
            @AuthenticationPrincipal CustomAdminDetails superAdmin,
            @RequestBody SuperAdminRequestDto dto) {
        adminService.rejectAdmin(dto, superAdmin.getAdmin());
        return ResponseEntity.ok(ApiResponse.of(SuccessStatus._OK, "권한 회수가 완료되었습니다."));
    }

    @PatchMapping("/reset-password")
    @Operation(summary = "임시 비밀번호 설정", description = "슈퍼 어드민이 특정 관리자의 비밀번호를 초기화")
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @AuthenticationPrincipal CustomAdminDetails superAdmin,
            @RequestBody SuperAdminPasswordResetDto dto) {
        adminService.resetPasswordBySuperAdmin(dto, superAdmin.getAdmin());
        return ResponseEntity.ok(ApiResponse.of(SuccessStatus._OK, "임시 비밀번호가 설정되었습니다."));
    }
}
