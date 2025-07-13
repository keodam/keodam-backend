package com.keodam.keodam_backend.app.domain.admin.security;

import com.keodam.keodam_backend.app.domain.admin.domain.Admin;
import com.keodam.keodam_backend.app.domain.admin.domain.CustomAdminDetails;
import com.keodam.keodam_backend.app.domain.admin.repository.AdminRepository;
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

        if (request.getRequestURI().equals("/admin/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        Optional<String> accessToken = jwtService.extractAccessToken(request)
                .filter(jwtService::isTokenValid);

        accessToken.flatMap(s -> jwtService.extractEmail(s)
                .flatMap(adminRepository::findByEmail)).ifPresent(this::saveAuthentication);

        filterChain.doFilter(request, response);
    }

    private void saveAuthentication(Admin admin) {
        CustomAdminDetails adminDetails = new CustomAdminDetails(admin);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                adminDetails,
                null,
                adminDetails.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
