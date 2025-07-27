package com.keodam.keodam_backend.term.domain;

import com.keodam.keodam_backend.exception.GeneralException;
import com.keodam.keodam_backend.global.code.status.ErrorStatus;

import java.util.Arrays;

public enum TermType {

    PRIVACY_POLICY("privacy-policy"),
    MARKETING_AGREEMENT("marketing-agreement"),
    TERMS_OF_SERVICE("term-of-service");

    private final String code;

    TermType(String str) {

        this.code = str;
    }

    public static TermType from(String code) {

        return Arrays.stream(values())
                .filter(e -> e.code.equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new GeneralException(ErrorStatus.TERM_NOT_FOUND));
    }
}
