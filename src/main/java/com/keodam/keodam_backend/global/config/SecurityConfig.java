package com.keodam.keodam_backend.global.config;

import com.keodam.keodam_backend.app.repository.UserRepository;
import com.keodam.keodam_backend.global.security.JwtAuthenticationProcessingFilter;
import com.keodam.keodam_backend.global.security.JwtService;
import com.keodam.keodam_backend.oauth.domain.CustomIdTokenUser;
import com.keodam.keodam_backend.oauth.service.IdTokenService;
import com.keodam.keodam_backend.oauth.service.handler.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final IdTokenLoginSuccessHandler idTokenLoginSuccessHandler;
    private final IdTokenLoginFailureHandler idTokenLoginFailureHandler;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final IdTokenService idTokenService;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                // 세션 사용X, JWT 사용
                .sessionManagement(session-> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/signup").authenticated())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/**").permitAll());


        http .addFilterBefore(requestHeaderAuthenticationFilter(), BasicAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthenticationProcessingFilter(), RequestHeaderAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public RequestHeaderAuthenticationFilter requestHeaderAuthenticationFilter() {
        RequestMatcher requestMatcher = new AntPathRequestMatcher("/auth/login");
        RequestHeaderAuthenticationFilter filter = new RequestHeaderAuthenticationFilter();

        filter.setRequiresAuthenticationRequestMatcher(requestMatcher);
        filter.setPrincipalRequestHeader("id_token");
        filter.setAuthenticationManager(authenticationManager());
        filter.setAuthenticationSuccessHandler(idTokenLoginSuccessHandler);
        filter.setAuthenticationFailureHandler(idTokenLoginFailureHandler);

        return filter;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return authentication -> {
            String token = (String) authentication.getPrincipal();
            try {
                CustomIdTokenUser user = idTokenService.loadUserByAccessToken(token);

                // PreAuthenticatedAuthenticationToken 생성
                return new PreAuthenticatedAuthenticationToken(
                        user,
                        token,
                        user.getAuthorities()
                );
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        };
    }

    @Bean
    public JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter() {
        JwtAuthenticationProcessingFilter jwtAuthenticationFilter = new JwtAuthenticationProcessingFilter(jwtService, userRepository);
        return jwtAuthenticationFilter;
    }
}
