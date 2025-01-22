package com.keodam.keodam_backend.mypage.dto.response;

import lombok.Builder;

public class EmailResponseDto {

    private String code;

    @Builder
    public EmailResponseDto(String code) {
        this.code = code;
    }
}
