package com.keodam.keodam_backend.mypage.payment.controller;

import com.keodam.keodam_backend.global.ApiResponse;
import com.keodam.keodam_backend.mypage.payment.dto.request.PaymentRequestDto;
import com.keodam.keodam_backend.mypage.payment.dto.response.PaymentResponseDto;
import com.keodam.keodam_backend.mypage.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage/payment")
@Tag(name = "User Payment", description = "Payment API")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/verify")
    public ApiResponse<PaymentResponseDto> createPayment(@RequestBody PaymentRequestDto paymentRequestDto) {
        return ApiResponse.onSuccess(paymentService.verifyAndSavePayment(paymentRequestDto));
    }
}
