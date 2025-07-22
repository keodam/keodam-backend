package com.keodam.keodam_backend.global.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@Slf4j
@Configuration
@EnableConfigurationProperties(OidcProperties.class)
@RequiredArgsConstructor
public class JwtDecoderConfig {

    private final OidcProperties oidcProperties;

    @Bean
    public JwtDecoder googleJwtDecoder() {
        return buildDecoder("google");
    }

    @Bean
    public JwtDecoder kakaoJwtDecoder() {
        return buildDecoder("kakao");
    }

    @Bean
    public JwtDecoder appleJwtDecoder() {
        return buildDecoder("apple");
    }

    private JwtDecoder buildDecoder(String providerName) {

        OidcProperties.Provider provider = oidcProperties.providers().get(providerName);

        NimbusJwtDecoder decoder = NimbusJwtDecoder
                .withJwkSetUri(provider.jwkSetUri())
                .build();

        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(provider.issuer());
        OAuth2TokenValidator<Jwt> audienceValidator = new AudienceValidator(provider.audiences());

        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator));
        return decoder;
    }
}
