package com.keodam.keodam_backend.app.user.dto;

public record SignupStatusResponseDto (
        boolean hasNickname,
        boolean hasProfileImage,
        boolean hasRole
){
}