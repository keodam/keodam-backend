package com.keodam.keodam_backend.oidc.controller;

import com.keodam.keodam_backend.global.ApiResponse;
import com.keodam.keodam_backend.global.security.JwtService;
import com.keodam.keodam_backend.oidc.dto.IdTokenRequest;
import com.keodam.keodam_backend.oidc.dto.IdTokenResponse;
import com.keodam.keodam_backend.oidc.service.IdTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Tag(name = "User OIDC Login", description = "OIDC Login API")
public class AuthController {

    private final IdTokenService idTokenService;

    @PostMapping("/api/auth/login")
    @Operation(summary = "소셜 로그인", description = "OIDC 전송하여 로그인하는 소셜 로그인 API")
    public ResponseEntity<IdTokenResponse> login(@RequestBody IdTokenRequest idTokenRequest) {

        Pair<IdTokenResponse, String> res = idTokenService.loadUserByOidcIdToken(idTokenRequest.provider(), idTokenRequest.idToken());

        return ResponseEntity.ok()
                .header("Authorization", res.getSecond())
                .body(res.getFirst());
    }
}
