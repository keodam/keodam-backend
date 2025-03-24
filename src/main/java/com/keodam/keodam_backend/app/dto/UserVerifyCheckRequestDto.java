package com.keodam.keodam_backend.app.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
// 특정 번호로 전송한 인증 코드를 검증 및 저장하기 위한 Dto
@Getter
@NoArgsConstructor
public class UserVerifyCheckRequestDto {
    private String phoneNumber;
    private String code;
    private Boolean userGender;
    private String userRealName;
    private String userBirth;
    private String userName;
}
