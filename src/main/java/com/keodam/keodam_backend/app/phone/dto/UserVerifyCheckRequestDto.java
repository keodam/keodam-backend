package com.keodam.keodam_backend.app.phone.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
// 특정 번호로 전송한 인증 코드를 검증 및 저장하기 위한 Dto
@Getter
@NoArgsConstructor
public class UserVerifyCheckRequestDto {
    private String phoneNumber;
    private String code;
    private String userRealName;
    private Boolean userGender;
    private String userBirth;
}
