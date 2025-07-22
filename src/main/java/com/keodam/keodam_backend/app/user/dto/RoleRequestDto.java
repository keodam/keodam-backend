package com.keodam.keodam_backend.app.user.dto;

import com.keodam.keodam_backend.app.user.domain.RoleType;

public record RoleRequestDto (RoleType roleType) {
}