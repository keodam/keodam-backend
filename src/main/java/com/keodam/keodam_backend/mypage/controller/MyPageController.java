package com.keodam.keodam_backend.mypage.controller;

import com.keodam.keodam_backend.global.ApiResponse;
import com.keodam.keodam_backend.mypage.dto.request.EmailRequestDto;
import com.keodam.keodam_backend.mypage.service.MailSendService;
import com.keodam.keodam_backend.mypage.service.MyPageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/mypage")
public class MyPageController {

    private final MyPageService myPageService;
    private final MailSendService mailSendService;

//    @GetMapping("/file")
//    public ApiResponse authenticateFile(Principal principal,
//                                        @RequestPart(name = "file")) {
//
//    }
//
    @GetMapping("/checkmailsend")
    public ApiResponse authenticateEmail(@RequestBody @Valid EmailRequestDto dto) {

        return ApiResponse.onSuccess(  mailSendService.checkEmail(dto.getEmail()));

    }
}
