package com.keodam.keodam_backend.mypage.bootpay.service;

import com.keodam.keodam_backend.exception.GeneralException;
import com.keodam.keodam_backend.global.code.status.ErrorStatus;
import com.keodam.keodam_backend.mypage.payment.dto.response.BootpayConfirmResponse;
import jakarta.transaction.Transactional;
import kr.co.bootpay.pg.Bootpay;
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
    public BootpayConfirmResponse confirm(String receiptId) {
        try {
            getBootpayToken();
            HashMap<String, Object> confirm = bootpay.confirm(receiptId);
            if (confirm.get("error_code") != null) {
                throw new GeneralException(ErrorStatus.BOOTPAY_CONFIRM_FAILED);
            }
            return BootpayConfirmResponse.from(confirm);
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.BOOTPAY_CONFIRM_EXCEPTION);
        }
    }

    private void getBootpayToken() throws Exception {
        this.bootpay = new Bootpay(applicationKey, privateKey);
        HashMap token = bootpay.getAccessToken();
        if (token.get("error_code") != null) {
            throw new GeneralException(ErrorStatus.BOOTPAY_TOKEN_FAILED);
        }
    }

    private void getBootpayReceipt(String receiptId) {
        try {
            getBootpayToken();
            HashMap res = bootpay.getReceipt(receiptId);
            if (res.get("error_code") != null) {
                throw new GeneralException(ErrorStatus.BOOTPAY_CONFIRM_FAILED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
