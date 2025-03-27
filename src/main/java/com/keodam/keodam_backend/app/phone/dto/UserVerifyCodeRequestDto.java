package com.keodam.keodam_backend.app.phone.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
// 특정 번호로 인증 코드를 요청하기 위한 Dto
@Getter
@NoArgsConstructor
public class UserVerifyCodeRequestDto {
    private String phoneNumber;
    private Boolean userGender;
    private String userRealName;
    private String userBirth;
}
