package com.keodam.keodam_backend.app.admin.security;

import com.keodam.keodam_backend.app.admin.domain.Admin;
import com.keodam.keodam_backend.app.admin.domain.CustomAdminDetails;
import com.keodam.keodam_backend.app.admin.repository.AdminRepository;
import com.keodam.keodam_backend.global.security.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
public class AdminJwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final AdminRepository adminRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        // 로그인은 필터 제외
        if (requestURI.equals("/admin/login") || requestURI.equals("/admin/register")) {
            filterChain.doFilter(request, response);
            return;
        }

        Optional<String> accessToken = jwtService.extractAccessToken(request)
                .filter(jwtService::isTokenValid);

        accessToken.flatMap(jwtService::extractEmail)
                .flatMap(adminRepository::findByEmail)
                .ifPresent(this::saveAuthentication);

        filterChain.doFilter(request, response);
    }

    private void saveAuthentication(Admin admin) {
        CustomAdminDetails adminDetails = new CustomAdminDetails(admin);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        adminDetails,
                        null,
                        adminDetails.getAuthorities()
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
