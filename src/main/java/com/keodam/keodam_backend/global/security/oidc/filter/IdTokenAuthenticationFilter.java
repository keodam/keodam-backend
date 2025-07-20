package com.keodam.keodam_backend.global.security.oidc.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.keodam.keodam_backend.global.security.oidc.IdTokenAuthenticationToken;
import com.keodam.keodam_backend.global.security.oidc.IdTokenRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

public class IdTokenAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final ObjectMapper objectMapper;

    public IdTokenAuthenticationFilter(AuthenticationManager authenticationManager, ObjectMapper objectMapper) {

        super(new AntPathRequestMatcher("/api/auth/login", "POST"));
        setAuthenticationManager(authenticationManager);
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("지원하지 않는 메소드: " + request.getMethod());
        }

        IdTokenRequest requestBody;
        try {

            requestBody = objectMapper.readValue(request.getInputStream(), IdTokenRequest.class);
        } catch (Exception e) {
            throw new BadCredentialsException("잘못된 요청 바디", e);
        }

        IdTokenAuthenticationToken authRequest = new IdTokenAuthenticationToken(requestBody);

        return this.getAuthenticationManager().authenticate(authRequest);
    }
}