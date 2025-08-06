package com.keodam.keodam_backend.mypage.roulette.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CoffeeExchangeRequest {
    @NotBlank(message = "휴대폰 번호를 입력해주세요.")
    private String phoneNumber;

    @NotNull(message = "수량을 입력해주세요.")
    @Min(value = 1, message = "수량은 1개 이상이어야 합니다.")
    private Integer quantity;
}
