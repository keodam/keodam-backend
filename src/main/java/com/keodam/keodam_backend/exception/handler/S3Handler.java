package com.keodam.keodam_backend.exception.handler;

import com.keodam.keodam_backend.exception.GeneralException;
import com.keodam.keodam_backend.global.code.BaseErrorCode;

public class S3Handler extends GeneralException {
    public S3Handler(BaseErrorCode code) {
        super(code);
    }
}
