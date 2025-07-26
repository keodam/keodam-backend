package com.keodam.keodam_backend.term.dto;

import jakarta.validation.constraints.AssertTrue;

public record TermsAgreementsRequest(

        @AssertTrue(message = "개인정보 처리방침에 동의해야 합니다.")
        boolean privacyPolicy,
        Integer privacyVersion,

        @AssertTrue(message = "서비스 이용약관에 동의해야 합니다.")
        boolean termsOfService,
        Integer serviceVersion,

        boolean marketingAgreement,
        Integer marketingVersion
) {}
