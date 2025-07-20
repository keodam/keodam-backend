package com.keodam.keodam_backend.oidc.controller;

import com.keodam.keodam_backend.global.security.oidc.IdTokenRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "User OIDC Login", description = "OIDC Login API")
public class AuthController {

    @PostMapping("/api/auth/login")
    @Operation(summary = "소셜 로그인", description = "OIDC 전송하여 로그인하는 소셜 로그인 API")
    public void login(@RequestBody IdTokenRequest idTokenRequest) {
    }
}
