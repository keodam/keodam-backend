package com.keodam.keodam_backend.app.domain.admin.dto;

import com.keodam.keodam_backend.app.domain.admin.domain.RoleTypeAdmin;

public record AdminResponseDto(
        String name,
        String email,
        RoleTypeAdmin roleType
) {}

