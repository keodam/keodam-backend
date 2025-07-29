package com.keodam.keodam_backend.mypage.roulette.controller;

import com.keodam.keodam_backend.global.ApiResponse;
import com.keodam.keodam_backend.global.code.status.SuccessStatus;
import com.keodam.keodam_backend.mypage.roulette.dto.CoffeeExchangeRequest;
import com.keodam.keodam_backend.mypage.roulette.dto.RouletteStatusResponse;
import com.keodam.keodam_backend.mypage.roulette.dto.SpinResultResponse;
import com.keodam.keodam_backend.mypage.roulette.service.RouletteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Mypage Roulette", description = "마이페이지 룰렛 기능 API")
@RestController
@RequestMapping("/api/mypage/roulette")
@RequiredArgsConstructor
public class RouletteController {

    private final RouletteService rouletteService;

    @Operation(summary = "룰렛 페이지 정보 조회", description = "보유한 룰렛 이용권과 커피 교환권 개수를 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<RouletteStatusResponse>> getRouletteStatus(
            Authentication authentication) {
        String email = authentication.getName();
        RouletteStatusResponse response = rouletteService.getRouletteStatus(email);
        return ResponseEntity.ok(ApiResponse.of(SuccessStatus._OK, response));
    }

    @Operation(summary = "룰렛 돌리기", description = "룰렛 이용권을 사용하여 룰렛을 돌립니다.")
    @PostMapping("/spin")
    public ResponseEntity<ApiResponse<SpinResultResponse>> spinRoulette(
            Authentication authentication) {
        String email = authentication.getName();
        SpinResultResponse response = rouletteService.spinRoulette(email);
        return ResponseEntity.ok(ApiResponse.of(SuccessStatus._OK, response));
    }

    @Operation(summary = "커피 기프티콘 교환 신청", description = "보유한 커피 교환권을 사용하여 기프티콘을 신청합니다.")
    @PostMapping("/exchange")
    public ResponseEntity<ApiResponse<String>> requestCoffeeExchange(
            Authentication authentication,
            @Valid @RequestBody CoffeeExchangeRequest request) {
        String email = authentication.getName();
        rouletteService.requestCoffeeExchange(email, request);
        return ResponseEntity.ok(ApiResponse.of(SuccessStatus._OK, "커피 교환권 신청이 완료되었습니다."));
    }
}