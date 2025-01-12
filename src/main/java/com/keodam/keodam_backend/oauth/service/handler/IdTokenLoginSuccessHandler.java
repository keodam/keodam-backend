package com.keodam.keodam_backend.oauth.service.handler;

import com.keodam.keodam_backend.global.security.JwtService;
import com.keodam.keodam_backend.oauth.domain.CustomIdTokenUser;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class IdTokenLoginSuccessHandler implements AuthenticationSuccessHandler {

    final private JwtService jwtService;
    private static final String BEARER = "Bearer ";
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("완료");
        CustomIdTokenUser idTokenUser = (CustomIdTokenUser) authentication.getPrincipal();
        response.setStatus(HttpServletResponse.SC_OK);
        String accessToken = jwtService.createAccessToken(idTokenUser.getEmail());
        String refreshToken = jwtService.createRefreshToken();
        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
        jwtService.updateRefreshToken(idTokenUser.getEmail(), refreshToken);
        response.getWriter().flush();

    }

}

