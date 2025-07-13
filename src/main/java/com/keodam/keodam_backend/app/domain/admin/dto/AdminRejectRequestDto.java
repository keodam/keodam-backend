package com.keodam.keodam_backend.app.domain.admin.dto;

public record AdminRejectRequestDto(
        String superAdminEmail,
        Long targetAdminId) {}
