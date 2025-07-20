package com.keodam.keodam_backend.app.admin.dto;

public record AdminPasswordUpdateDto(
        String currentPassword, String newPassword
) {}
