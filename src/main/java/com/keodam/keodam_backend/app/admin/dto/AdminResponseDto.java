package com.keodam.keodam_backend.app.admin.dto;

import com.keodam.keodam_backend.app.admin.domain.RoleTypeAdmin;

public record AdminResponseDto(
        String name,
        String email,
        RoleTypeAdmin roleType
) {}
