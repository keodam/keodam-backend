package com.keodam.keodam_backend.mypage.controller;

import com.keodam.keodam_backend.mypage.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;





}
