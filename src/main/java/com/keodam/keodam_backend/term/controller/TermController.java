package com.keodam.keodam_backend.term.controller;

import com.keodam.keodam_backend.global.ApiResponse;
import com.keodam.keodam_backend.term.service.TermService;
import com.keodam.keodam_backend.term.TermsAgreementsRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "약관 동의", description = "약관 동의 API")
public class TermController {

    private final TermService termService;

    @PostMapping("/terms-agreements")
    public ApiResponse<Boolean> agreeTerm(Authentication authentication, @RequestBody TermsAgreementsRequest request){

        String email = authentication.getName();
        return ApiResponse.onSuccess(termService.agree(email, request));
    }

    @GetMapping("/terms/{type}")
    public ApiResponse<String> getTerm(@PathVariable String type) {

        return ApiResponse.onSuccess(termService.getLatestTerm(type));
    }
}
