package com.keodam.keodam_backend.app.user.dto.res;

import com.keodam.keodam_backend.app.domain.RoleType;
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
