package com.keodam.keodam_backend.app.domain.admin.dto;

public record SuperAdminPasswordResetDto(
        String targetAdminEmail, String temporaryPassword
) {}
