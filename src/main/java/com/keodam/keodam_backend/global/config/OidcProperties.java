package com.keodam.keodam_backend.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "oidc")
public record OidcProperties(
        Map<String, Provider> providers
) {

    public record Provider(String issuer, List<String> audiences, String jwkSetUri) {}
}
