package com.keodam.keodam_backend.mypage.bootpay.service;

import jakarta.transaction.Transactional;
import kr.co.bootpay.Bootpay;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class BootpayService {

    private Bootpay bootpay;

    @Value("${bootpay.application-key}")
    private String applicationKey;

    @Value("${bootpay.private-key}")
    private String privateKey;

    @Transactional
    public void confirm(String receiptId) {
        try {
            getBootpayToken();
            HashMap confirm = bootpay.confirm(receiptId);
            if (confirm.get("error_code") != null) {
                throw new RuntimeException("Bootpay 승인 실패: " + confirm.get("message"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getBootpayToken() throws Exception {
        this.bootpay = new Bootpay(applicationKey, privateKey);
        HashMap token = bootpay.getAccessToken();
        if (token.get("error_code") != null) {
            throw new RuntimeException("Bootpay 토큰 발급 실패: " + token.get("message"));
        }
    }

    private void getBootpayReceipt(String receiptId) {
        try {
            getBootpayToken();
            HashMap res = bootpay.getReceipt(receiptId);
            if (res.get("error_code") != null) {
                System.out.println("goGetToken success" + res);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
