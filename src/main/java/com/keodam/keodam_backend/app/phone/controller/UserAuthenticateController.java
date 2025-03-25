package com.keodam.keodam_backend.app.phone.controller;

import com.keodam.keodam_backend.app.phone.dto.UserVerifyCheckRequestDto;
import com.keodam.keodam_backend.app.phone.dto.UserVerifyCodeRequestDto;
import com.keodam.keodam_backend.app.phone.service.UserAuthenticateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    @Operation(summary = "인증번호 요청", description = "사용자 정보 기반 인증요청 API")
    @Parameter( name = "임시 auth_token", description = "Bearer 토큰을 제외한 JWT Token",
            required = false, in = ParameterIn.HEADER )
    public ResponseEntity<Object> requestVerifyCode(@RequestBody UserVerifyCodeRequestDto dto) {
        return userAuthenticateService.startVerification(dto);
    }

    @PostMapping("/check")
    @Operation(summary = "인증번호 검증", description = "인증코드 기반 검증요청 API")
    @Parameter( name = "임시 auth_token", description = "Bearer 토큰을 제외한 JWT Token",
            required = false, in = ParameterIn.HEADER )
    public ResponseEntity<Object> checkVerifyCode(@RequestBody UserVerifyCheckRequestDto dto) {
        return userAuthenticateService.checkVerification(dto);
    }
}
