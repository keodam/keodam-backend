package com.keodam.keodam_backend.mypage.roulette.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SpinResultResponse {
    private String result;
    private int remainingRouletteCoupons;
    private String itemIndex;
}
