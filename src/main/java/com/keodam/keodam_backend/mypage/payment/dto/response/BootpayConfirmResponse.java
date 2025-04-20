package com.keodam.keodam_backend.mypage.payment.dto.response;

import lombok.Getter;
import java.util.HashMap;

@Getter
public class BootpayConfirmResponse {
    private String receiptId;
    private String method;
    private int price;

    private BootpayConfirmResponse(String receiptId, String method, int price) {
        this.receiptId = receiptId;
        this.method = method;
        this.price = price;
    }

    public static BootpayConfirmResponse from(HashMap<String, Object> confirmMap) {
        String receiptId = (String) confirmMap.get("receiptId");
        String method = (String) confirmMap.get("method");
        int price = (int) confirmMap.get("price");
        return new BootpayConfirmResponse(receiptId, method, price);
    }
}
