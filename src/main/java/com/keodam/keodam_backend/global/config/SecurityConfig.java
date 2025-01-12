package com.keodam.keodam_backend.global.config;

import com.keodam.keodam_backend.app.repository.UserRepository;
import com.keodam.keodam_backend.global.security.IdTokenAuthenticationFilter;
import com.keodam.keodam_backend.global.security.JwtAuthenticationProcessingFilter;
import com.keodam.keodam_backend.global.security.JwtService;
import com.keodam.keodam_backend.oauth.domain.CustomIdTokenUser;
import com.keodam.keodam_backend.oauth.service.IdTokenService;
import com.keodam.keodam_backend.oauth.service.handler.IdTokenLoginSuccessHandler;
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final IdTokenLoginSuccessHandler idTokenLoginSuccessHandler;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final IdTokenService idTokenService;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
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
        RequestHeaderAuthenticationFilter filter = new RequestHeaderAuthenticationFilter();
        filter.setPrincipalRequestHeader("id_token");
        filter.setAuthenticationManager(authenticationManager());
        filter.setAuthenticationSuccessHandler(idTokenLoginSuccessHandler);
        return filter;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return authentication -> {
            String token = (String) authentication.getPrincipal();
            CustomIdTokenUser user = idTokenService.loadUserByAccessToken(token);

            // PreAuthenticatedAuthenticationToken 생성
            return new PreAuthenticatedAuthenticationToken(
                    user,
                    token,
                    user.getAuthorities()
            );
        };
    }

    @Bean
    public JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter() {
        JwtAuthenticationProcessingFilter jwtAuthenticationFilter = new JwtAuthenticationProcessingFilter(jwtService, userRepository);
        return jwtAuthenticationFilter;
    }
}
