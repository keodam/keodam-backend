package com.keodam.keodam_backend.global.util;

public class TwilioUtils {

    public static String formatPhone(String phone) {
        String digits = phone.replaceAll("[^0-9]", "");
        if (digits.startsWith("0")) {
            return "+82" + digits.substring(1);
        }
        return digits.startsWith("+") ? digits : "+" + digits;
    }
}
