package com.keodam.keodam_backend.app.user.referral.controller;

import com.keodam.keodam_backend.app.domain.User;
import com.keodam.keodam_backend.app.user.referral.dto.ReferralRequestDto;
import com.keodam.keodam_backend.app.user.referral.service.ReferralService;
import com.keodam.keodam_backend.app.user.service.UserService;
import com.keodam.keodam_backend.global.code.status.ErrorStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Tag(name = "User", description = "User API")
public class ReferralController {

    private final ReferralService referralService;
    private final UserService userService;

    @PostMapping("/referral")
    @Operation(summary = "추천인 등록", description = "추천인 등록 API", security = @SecurityRequirement(name = "Authorization"))
    public ResponseEntity<?> registerReferral(
            @RequestBody ReferralRequestDto referralRequestDto,
            Authentication authentication) {

        String email = authentication.getName();

        User sponsor = userService.findByEmail(email)
                .orElse(null);

        if (sponsor == null) {
            return ResponseEntity.status(ErrorStatus.USER_NOT_FOUND.getHttpStatus())
                    .body(ErrorStatus.USER_NOT_FOUND.getReasonHttpStatus());
        }

        return referralService.registerReferral(sponsor, referralRequestDto.getRefereeNickname());
    }
}
