package com.keodam.keodam_backend.oidc.dto;

import com.keodam.keodam_backend.app.user.domain.RoleType;

public record IdTokenResponse(RoleType page,
                              String refreshToken) {}
