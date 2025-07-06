package com.keodam.keodam_backend.mypage.payment.dto.request;

import lombok.Getter;

@Getter
public class PaymentRequestDto {
    private String receiptId;
    private int price;
    private Long userId;
    private int beanAmount;
}
