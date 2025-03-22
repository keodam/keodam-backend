package com.keodam.keodam_backend.mypage.controller;

import com.keodam.keodam_backend.global.ApiResponse;
import com.keodam.keodam_backend.mypage.dto.request.EmailRequestDto;
import com.keodam.keodam_backend.mypage.service.MailSendService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage/email")
public class EmailController {

    private final MailSendService mailSendService;

    @GetMapping
    public ApiResponse authenticateEmail(@RequestBody @Valid EmailRequestDto emailDto) {
        return ApiResponse.onSuccess(mailSendService.checkEmail(emailDto.getEmail()));
    }
}
