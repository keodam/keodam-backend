package com.keodam.keodam_backend.app.admin.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AdminRegisterDto(
        @NotBlank String name,
        @Email String email,
        @NotBlank String password
) {}
