package com.keodam.keodam_backend.app.user.referral.controller;

import com.keodam.keodam_backend.app.user.domain.User;
import com.keodam.keodam_backend.app.user.referral.dto.ReferralRequestDto;
import com.keodam.keodam_backend.app.user.referral.service.ReferralService;
import com.keodam.keodam_backend.app.user.service.UserService;
import com.keodam.keodam_backend.exception.GeneralException;
import com.keodam.keodam_backend.global.ApiResponse;
import com.keodam.keodam_backend.global.code.status.ErrorStatus;
import com.keodam.keodam_backend.global.code.status.SuccessStatus;
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
    public ResponseEntity<ApiResponse<String>> registerReferral(
            @RequestBody ReferralRequestDto referralRequestDto,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(ErrorStatus.UNAUTHORIZED.getHttpStatus())
                    .body(ApiResponse.onFailure(
                            ErrorStatus.UNAUTHORIZED.getCode(),
                            ErrorStatus.UNAUTHORIZED.getMessage(),
                            null
                    ));
        }

        String email = authentication.getName();
        User sponsor = userService.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        String message = referralService.registerReferral(sponsor, referralRequestDto.getRefereeNickname());

        return ResponseEntity.status(SuccessStatus._OK.getHttpStatus())
                .body(ApiResponse.of(SuccessStatus._OK, message));
    }
}
