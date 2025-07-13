package com.keodam.keodam_backend.app.domain.admin.dto;

import lombok.Getter;

@Getter
public class AdminRegisterDto {
    private String name;
    private String password;
    private String email;
}
