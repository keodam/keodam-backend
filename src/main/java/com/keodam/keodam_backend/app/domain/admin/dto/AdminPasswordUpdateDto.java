package com.keodam.keodam_backend.app.domain.admin.dto;

public record AdminPasswordUpdateDto(
        String currentPassword, String newPassword
) {}
