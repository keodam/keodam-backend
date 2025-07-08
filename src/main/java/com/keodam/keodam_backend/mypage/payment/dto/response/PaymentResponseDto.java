package com.keodam.keodam_backend.mypage.payment.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Builder
@Getter
public class PaymentResponseDto {
    private String receiptId;
    private int beanAmount;
    private int price;
    private String method;
    private LocalDateTime paidAt;
    private int totalBeans;
}
