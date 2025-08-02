package com.keodam.keodam_backend.global.security;

import com.keodam.keodam_backend.app.user.domain.User;
import com.keodam.keodam_backend.app.user.repository.UserRepository;
import com.keodam.keodam_backend.exception.GeneralException;
import com.keodam.keodam_backend.global.code.status.ErrorStatus;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (request.getRequestURI().equals("/api/auth/login")) {

            filterChain.doFilter(request, response); // "/login" 요청이 들어오면, 다음 필터 호출
            return; // return으로 이후 현재 필터 진행 막기 (안해주면 아래로 내려가서 계속 필터 진행시킴)
        }

        checkAccessTokenAndAuthentication(request, response, filterChain);
    }

    private String reIssueRefreshToken(User user) {

        String reIssuedRefreshToken = jwtService.createRefreshToken();
        user.updateRefreshToken(reIssuedRefreshToken);
        userRepository.saveAndFlush(user);
        return reIssuedRefreshToken;
    }

    public void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response,
                                                  FilterChain filterChain) throws ServletException, IOException {
        jwtService.extractAccessToken(request)
                .filter(jwtService::isTokenValid)
                .flatMap(accessToken -> jwtService.extractEmail(accessToken)
                        .flatMap(userRepository::findByEmail)).ifPresent(this::saveAuthentication);

        filterChain.doFilter(request, response);
    }

    public void saveAuthentication(User myUser) {

        JwtPrincipal jwtPrincipal = JwtPrincipal.builder()
                                        .userId(myUser.getId())
                                        .role(myUser.getRoleType()).build();
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(jwtPrincipal, null,
                        authoritiesMapper.mapAuthorities(jwtPrincipal.getAuthorities()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
