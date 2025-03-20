package com.keodam.keodam_backend.app.user.dto.res;

public record SignupStatusResponseDto (
        boolean hasNickname,
        boolean hasProfileImage,
        boolean hasRole
){

}