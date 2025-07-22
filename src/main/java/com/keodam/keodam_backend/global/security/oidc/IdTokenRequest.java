package com.keodam.keodam_backend.global.security.oidc;

import jakarta.validation.constraints.NotBlank;

public record IdTokenRequest(
        @NotBlank(message = "ID Token은 필수 값입니다.")
        String idToken,
        @NotBlank(message = "provider는 필수 값입니다.")
        String provider) {}
