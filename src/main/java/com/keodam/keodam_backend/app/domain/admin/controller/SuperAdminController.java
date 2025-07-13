package com.keodam.keodam_backend.app.domain.admin.controller;

import com.keodam.keodam_backend.app.domain.admin.domain.Admin;
import com.keodam.keodam_backend.app.domain.admin.service.AdminService;
import com.keodam.keodam_backend.app.domain.admin.dto.AdminApprovalRequestDto;
import com.keodam.keodam_backend.app.domain.admin.dto.AdminListRequestDto;
import com.keodam.keodam_backend.app.domain.admin.dto.AdminRejectRequestDto;
import com.keodam.keodam_backend.global.ApiResponse;
import com.keodam.keodam_backend.global.code.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/super")
@Tag(name = "Super Admin", description = "Super Admin 전용 API")
public class SuperAdminController {

    private final AdminService adminService;

    @PatchMapping("/approve")
    @Operation(summary = "관리자 승인", description = "슈퍼어드민이 관리자 계정 승인")
    public ResponseEntity<ApiResponse<String>> approve(@RequestBody AdminApprovalRequestDto dto) {
        Admin superAdmin = adminService.getByEmail(dto.superAdminEmail());
        adminService.approveAdmin(dto.targetAdminId(), superAdmin);
        return ResponseEntity.ok(ApiResponse.of(SuccessStatus._OK, "승인이 완료되었습니다."));
    }

    @PatchMapping("/reject")
    @Operation(summary = "관리자 권한 회수", description = "관리자 권한 제거")
    public ResponseEntity<ApiResponse<String>> reject(@RequestBody AdminRejectRequestDto dto) {
        Admin superAdmin = adminService.getByEmail(dto.superAdminEmail());
        adminService.rejectAdmin(dto.targetAdminId(), superAdmin);
        return ResponseEntity.ok(ApiResponse.of(SuccessStatus._OK, "권한 회수가 완료되었습니다."));
    }

    @PostMapping("/all")
    @Operation(summary = "전체 관리자 조회", description = "관리자 목록 조회")
    public ResponseEntity<ApiResponse<List<Admin>>> getAllAdmins(@RequestBody AdminListRequestDto dto) {
        Admin requester = adminService.getByEmail(dto.superAdminEmail());
        List<Admin> admins = adminService.getAllAdmins(requester);
        return ResponseEntity.ok(ApiResponse.of(SuccessStatus._OK, admins));
    }
}
