package com.keodam.keodam_backend.app.admin.dto;

import com.keodam.keodam_backend.app.admin.domain.RoleTypeAdmin;

public record AdminLoginResponseDto(
        String name,
        String email,
        RoleTypeAdmin roleType,
        String accessToken,
        String refreshToken
) {}
