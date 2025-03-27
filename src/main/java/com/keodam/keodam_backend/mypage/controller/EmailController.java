package com.keodam.keodam_backend.mypage.controller;

import com.keodam.keodam_backend.global.ApiResponse;
import com.keodam.keodam_backend.mypage.dto.request.EmailRequestDto;
import com.keodam.keodam_backend.mypage.dto.response.EmailResponseDto;
import com.keodam.keodam_backend.mypage.service.MailSendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
@Tag(name = "EMAIL Verify", description = "EMAIL Verify API")
public class EmailController {

    private final MailSendService mailSendService;

    @PostMapping("/email")
    @Operation(summary = "이메일 인증번호 요청", description = "이메일 기반 인증요청 API", security = @SecurityRequirement(name = "Authorization"))
    public ApiResponse<EmailResponseDto> authenticateEmail(Authentication authentication,
                                                           @RequestBody @Valid EmailRequestDto.EmailSenderDto emailDto) {
        String email = authentication.getName();
        return ApiResponse.onSuccess(mailSendService.checkEmail(email, emailDto));
    }

    @PostMapping("/code")
    @Operation(summary = "이메일 인증번호 검증", description = "이메일 기반 인증코드 검증 API", security = @SecurityRequirement(name = "Authorization"))
    public ApiResponse<Boolean> authenticateCode(Authentication authentication, String code) {

        String email = authentication.getName();
        return ApiResponse.onSuccess(mailSendService.checkCode(email, code));
    }
}

