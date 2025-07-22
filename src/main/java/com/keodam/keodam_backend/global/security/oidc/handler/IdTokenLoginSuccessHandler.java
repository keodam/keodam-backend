package com.keodam.keodam_backend.global.security.oidc.handler;

import com.keodam.keodam_backend.global.security.JwtService;
import com.keodam.keodam_backend.global.security.oidc.domain.CustomUserDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class IdTokenLoginSuccessHandler implements AuthenticationSuccessHandler {

    final private JwtService jwtService;
    private static final String BEARER = "Bearer ";
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        CustomUserDetails idTokenUser = (CustomUserDetails) authentication.getPrincipal();

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String json = "{\"page\": \"" + idTokenUser.getRoleType() + "\"}";
        String accessToken = jwtService.createAccessToken(idTokenUser.getUsername());
        String refreshToken = jwtService.createRefreshToken();

        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
        jwtService.updateRefreshToken(idTokenUser.getUsername(), refreshToken);

        response.getWriter().write(json);
        response.getWriter().flush();
    }
}
