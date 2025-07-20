package com.keodam.keodam_backend.app.user.dto;

import com.keodam.keodam_backend.app.user.domain.RoleType;
import lombok.Builder;

@Builder
public record UserResponseDto (
        Long id,
        String nickname,
        String email,
        String profileUrl,
        RoleType roleType,
        boolean hasProfileImage,
        boolean hasRole
){
}
