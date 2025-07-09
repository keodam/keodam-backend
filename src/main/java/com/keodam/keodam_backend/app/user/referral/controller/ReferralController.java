package com.keodam.keodam_backend.app.user.referral.controller;

import com.keodam.keodam_backend.app.user.referral.dto.ReferralRequestDto;
import com.keodam.keodam_backend.app.user.referral.service.ReferralService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/user/referral")
@RequiredArgsConstructor
public class ReferralController {

    private final ReferralService referralService;

    @PostMapping
    public ResponseEntity<String> registerReferral(@RequestBody ReferralRequestDto dto){
        referralService.registerReferral(dto.getInviteeId(), dto.getReferrerNickname());
        return ResponseEntity.ok("추천인 등록 및 보상 지급 완료.");
    }
}
