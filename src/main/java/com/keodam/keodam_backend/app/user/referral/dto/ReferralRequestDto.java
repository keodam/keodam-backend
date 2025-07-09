package com.keodam.keodam_backend.app.user.referral.dto;

import lombok.Getter;

@Getter
public class ReferralRequestDto {
    private Long inviteeId;
    private String referrerNickname;
}
