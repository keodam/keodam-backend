package com.keodam.keodam_backend.mypage.roulette.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RouletteStatusResponse {
    private int rouletteCoupon;
    private int coffeCoupon;
}
