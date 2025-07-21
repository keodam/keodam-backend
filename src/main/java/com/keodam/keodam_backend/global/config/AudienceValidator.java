package com.keodam.keodam_backend.global.config;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

public class AudienceValidator implements OAuth2TokenValidator<Jwt> {

    private final List<String> expectedAudiences;

    public AudienceValidator(List<String> expectedAudiences) {
        this.expectedAudiences = expectedAudiences;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        for (String aud : jwt.getAudience()) {
            if (expectedAudiences.contains(aud)) {
                return OAuth2TokenValidatorResult.success();
            }
        }

        OAuth2Error error = new OAuth2Error(
                "invalid_token",
                "The required audience is missing",
                null
        );
        return OAuth2TokenValidatorResult.failure(error);
    }
}
