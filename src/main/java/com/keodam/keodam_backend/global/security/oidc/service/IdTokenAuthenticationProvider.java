package com.keodam.keodam_backend.global.security.oidc.service;

import com.keodam.keodam_backend.global.security.oidc.IdTokenAuthenticationToken;
import com.keodam.keodam_backend.global.security.oidc.IdTokenRequest;
import com.keodam.keodam_backend.global.security.oidc.domain.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor // 생성자 주입을 위한 Lombok 어노테이션
public class IdTokenAuthenticationProvider implements AuthenticationProvider {

    private final IdTokenService idTokenService;

    @Override
    public boolean supports(Class<?> authentication) {

        return IdTokenAuthenticationToken.class.isAssignableFrom(authentication);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        IdTokenAuthenticationToken token = (IdTokenAuthenticationToken) authentication;
        IdTokenRequest idTokenRequest = (IdTokenRequest) token.getPrincipal();

        String provider = idTokenRequest.provider();
        String idToken = idTokenRequest.idToken();

        CustomUserDetails userDetails = idTokenService.loadUserByOidcIdToken(provider, idToken);

        return new OAuth2AuthenticationToken(
                userDetails,
                userDetails.getAuthorities(),
                provider
        );
    }
}
