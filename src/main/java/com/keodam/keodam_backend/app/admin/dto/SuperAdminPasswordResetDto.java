package com.keodam.keodam_backend.app.admin.dto;

public record SuperAdminPasswordResetDto(
        String targetAdminEmail, String temporaryPassword
) {}
