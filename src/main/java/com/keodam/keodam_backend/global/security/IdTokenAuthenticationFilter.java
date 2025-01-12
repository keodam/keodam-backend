package com.keodam.keodam_backend.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.keodam.keodam_backend.oauth.domain.CustomIdTokenUser;
import com.keodam.keodam_backend.oauth.domain.IdTokenAttributes;
import com.keodam.keodam_backend.oauth.service.IdTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
public class IdTokenAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final IdTokenService idTokenService;

    public IdTokenAuthenticationFilter(IdTokenService idTokenService) {
        super(new AntPathRequestMatcher("/auth/login/**","POST")); // 로그인 엔드포인트
        this.idTokenService = idTokenService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
//        String idToken = request.getHeader("access_token");
        String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> body = objectMapper.readValue(requestBody, Map.class);

        String idToken = (String) body.get("access_token");
        if (idToken == null) return null;

        AntPathMatcher pathMatcher = new AntPathMatcher();
        String provider = pathMatcher.extractPathWithinPattern("/auth/login/**", request.getRequestURI()).toUpperCase();
        log.info("감지!");
        CustomIdTokenUser idTokenUser = idTokenService.loadUserByAccessToken(idToken);
        if(idTokenUser == null) throw new RuntimeException("Invalid ID Token");
        log.info("감지!2");
        PreAuthenticatedAuthenticationToken authentication =
                new PreAuthenticatedAuthenticationToken(idTokenUser, idToken, idTokenUser.getAuthorities());
        return this.getAuthenticationManager().authenticate(authentication);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.info("Authentication successful: {}", authResult.getName());
        super.successfulAuthentication(request, response, chain, authResult); // 성공 핸들러 호출
    }
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.error("Authentication failed: {}", failed.getMessage());
        super.unsuccessfulAuthentication(request, response, failed); // 실패 핸들러 호출
    }
}
