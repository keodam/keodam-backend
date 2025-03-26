package com.keodam.keodam_backend.oauth.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthTestControllerDto {
    private String accessToken;
    private String refreshToken;
}
