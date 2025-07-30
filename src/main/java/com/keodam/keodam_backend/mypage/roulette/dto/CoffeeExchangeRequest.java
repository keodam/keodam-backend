package com.keodam.keodam_backend.mypage.roulette.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CoffeeExchangeRequest {
    @NotBlank(message = "휴대폰 번호를 입력해주세요.")
    private String phoneNumber;
}
