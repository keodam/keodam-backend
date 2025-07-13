package com.keodam.keodam_backend.app.domain.admin.dto;

public record AdminApprovalRequestDto(String superAdminEmail, Long targetAdminId) {}
