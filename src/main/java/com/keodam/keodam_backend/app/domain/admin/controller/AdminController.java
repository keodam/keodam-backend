package com.keodam.keodam_backend.app.domain.admin.controller;

import com.keodam.keodam_backend.app.domain.admin.domain.Admin;
import com.keodam.keodam_backend.app.domain.admin.service.AdminService;
import com.keodam.keodam_backend.global.ApiResponse;
import com.keodam.keodam_backend.global.code.status.SuccessStatus;
import com.keodam.keodam_backend.app.domain.admin.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
        adminService.register(dto.getName(), dto.getEmail(), dto.getPassword());
        return ResponseEntity.status(SuccessStatus._OK.getHttpStatus())
                .body(ApiResponse.of(SuccessStatus._OK, "관리자 가입 요청이 완료되었습니다."));
    }

    @PostMapping("/login")
    @Operation(summary = "관리자 로그인", description = "정상 관리자만 로그인 가능")
    public ResponseEntity<ApiResponse<AdminResponseDto>> login(@RequestBody AdminLoginDto dto) {
        AdminResponseDto admin = adminService.login(dto.getEmail(), dto.getPassword());
        return ResponseEntity.ok(ApiResponse.of(SuccessStatus._OK, admin));
    }
}

