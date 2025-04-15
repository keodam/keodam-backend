package com.keodam.keodam_backend.mypage.certification.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EmailResponseDto {

    private String code;

    @Builder
    public EmailResponseDto(String code) {
        this.code = code;
    }
}
