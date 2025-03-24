package com.keodam.keodam_backend.app.controller;

import com.keodam.keodam_backend.app.dto.UserVerifyCheckRequestDto;
import com.keodam.keodam_backend.app.dto.UserVerifyCodeRequestDto;
import com.keodam.keodam_backend.app.service.UserAuthenticateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/authenticate")
@RequiredArgsConstructor
public class UserAuthenticateController {

    private final UserAuthenticateService userAuthenticateService;

    @PostMapping("/code")
    public ResponseEntity<Object> requestVerifyCode(@RequestBody UserVerifyCodeRequestDto dto) {
        return userAuthenticateService.startVerification(dto);
    }

    @PostMapping("/check")
    public ResponseEntity<Object> checkVerifyCode(@RequestBody UserVerifyCheckRequestDto dto) {
        return userAuthenticateService.checkVerification(dto);
    }
}
