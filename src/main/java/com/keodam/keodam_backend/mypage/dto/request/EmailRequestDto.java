package com.keodam.keodam_backend.mypage.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

public class EmailRequestDto {

    @Getter
    public static class EmailSenderDto {
        @NotNull
        @Email
        @NotEmpty(message = "이메일을 입력해 주세요.")
        private String email;
        private String documentType;
    }
}
