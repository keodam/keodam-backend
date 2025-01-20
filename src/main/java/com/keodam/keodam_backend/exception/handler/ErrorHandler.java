package com.keodam.keodam_backend.exception.handler;

import com.keodam.keodam_backend.global.code.BaseErrorCode;
import com.keodam.keodam_backend.exception.GeneralException;

public class ErrorHandler extends GeneralException {

    public ErrorHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
