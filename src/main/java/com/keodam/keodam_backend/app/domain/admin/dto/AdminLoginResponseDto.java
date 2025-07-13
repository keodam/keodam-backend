package com.keodam.keodam_backend.app.domain.admin.dto;

import com.keodam.keodam_backend.app.domain.admin.domain.RoleTypeAdmin;

public record AdminLoginResponseDto(
        String name,
        String email,
        RoleTypeAdmin roleType,
        String accessToken,
        String refreshToken
) {}
