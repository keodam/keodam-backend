package com.keodam.keodam_backend.term.controller;

import com.keodam.keodam_backend.global.ApiResponse;
import com.keodam.keodam_backend.term.dto.TermsAgreementsResponse;
import com.keodam.keodam_backend.term.service.TermService;
import com.keodam.keodam_backend.term.dto.TermsAgreementsRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "약관", description = "약관 API")
@RequestMapping("api/terms")
public class TermController {

    private final TermService termService;

    @PostMapping("/agreements")
    @Operation(summary = "약관 동의", description = "privacyPolicy, termOfService는 필수, marketingAgreement는 선택 사항 ", security = @SecurityRequirement(name = "Authorization"))
    public ApiResponse<Boolean> agreeTerm(Authentication authentication, @Valid @RequestBody TermsAgreementsRequest request){

        String email = authentication.getName();
        return ApiResponse.onSuccess(termService.agree(email, request));
    }

    @GetMapping("/{type}")
    @Operation(summary = "약관 내용", description = "약관을 3가지 타입 privacy-policy, term-of-service, marketing-agreement로 조회 가능")
    public ApiResponse<TermsAgreementsResponse> getTerm(@PathVariable String type) {

        return ApiResponse.onSuccess(termService.getLatestTerm(type));
    }
}
