package com.keodam.keodam_backend.term.dto;

import jakarta.validation.constraints.NotEmpty;

public record TermCreateRequest(

        @NotEmpty(message = "약관 타입 누락되었습니다.")
        String termType,

        @NotEmpty(message = "약관 내용이 누락되었습니다.")
        String content) {}
