package com.keodam.keodam_backend.app.phone.controller;

import com.keodam.keodam_backend.app.phone.dto.UserVerifyCheckRequestDto;
import com.keodam.keodam_backend.app.phone.dto.UserVerifyCodeRequestDto;
import com.keodam.keodam_backend.app.phone.service.UserAuthenticateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/authenticate")
@RequiredArgsConstructor
@Tag(name = "PHONE Verify", description = "PHONE Verify API")
public class UserAuthenticateController {

    private final UserAuthenticateService userAuthenticateService;

    @PostMapping("/code")
    @Operation(summary = "인증번호 요청", description = "사용자 정보 기반 인증요청 API", security = @SecurityRequirement(name = "Authorization"))
    public ResponseEntity<Object> requestVerifyCode(Authentication authentication, @RequestBody UserVerifyCodeRequestDto dto) {
        String email = authentication.getName(); // 여기서 이메일 추출
        return userAuthenticateService.startVerification(dto, email);
    }

    @PostMapping("/check")
    @Operation(summary = "인증번호 검증", description = "인증코드 기반 검증요청 API", security = @SecurityRequirement(name = "Authorization"))
    public ResponseEntity<Object> checkVerifyCode(Authentication authentication, @RequestBody UserVerifyCheckRequestDto dto) {
        String email = authentication.getName();
        return userAuthenticateService.checkVerification(dto, email);
    }
}
