package com.keodam.keodam_backend.global.config;


import com.keodam.keodam_backend.app.domain.admin.repository.AdminRepository;
import com.keodam.keodam_backend.app.domain.admin.security.AdminJwtAuthenticationFilter;
import com.keodam.keodam_backend.app.user.repository.UserRepository;
import com.keodam.keodam_backend.global.security.JwtAuthenticationProcessingFilter;
import com.keodam.keodam_backend.global.security.JwtService;
import com.keodam.keodam_backend.oauth.domain.CustomUserDetails;
import com.keodam.keodam_backend.oauth.service.IdTokenService;
import com.keodam.keodam_backend.oauth.service.handler.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    @Order(2) // Admin 체인 먼저 거치고 나서 User 체인 실행 (필수)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                // 세션 사용X, JWT 사용
                .sessionManagement(session-> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                                .requestMatchers(
                                        "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**",
                                        "/swagger-resources/**", "/webjars/**", "/api-test/**"
                                ).permitAll()
                                .requestMatchers("/signup").authenticated()
                                .anyRequest().permitAll()
                        // 개발 편의성을 위해 한시적으로 permitAll로 관리함.
                );
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
                CustomUserDetails user = idTokenService.loadUserByAccessToken(token);
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
        return new JwtAuthenticationProcessingFilter(jwtService, userRepository);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    // admin only filter chain series
    @Bean
    public AdminJwtAuthenticationFilter adminJwtAuthenticationFilter(AdminRepository adminRepository) {
        return new AdminJwtAuthenticationFilter(jwtService, adminRepository);
    }

    // super admin
    @Bean
    @Order(0)
    public SecurityFilterChain superAdminFilterChain(HttpSecurity http, AdminJwtAuthenticationFilter adminFilter) throws Exception {
        http
                .securityMatcher("/admin/super/**")  // 이 경로만 처리
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().hasRole("SUPER_ADMIN")  // 오직 SUPER_ADMIN만 접근 가능
                )
                .addFilterBefore(adminFilter, BasicAuthenticationFilter.class);

        return http.build();
    }

    // admin
    @Bean
    @Order(1)
    public SecurityFilterChain adminFilterChain(HttpSecurity http, AdminJwtAuthenticationFilter adminFilter) throws Exception {
        http
                .securityMatcher("/admin/**")
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/admin/login",
                                "/admin/register",
                                "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**",
                                "/swagger-resources/**", "/webjars/**", "/api-test/**"
                        ).permitAll()
                        .anyRequest().hasRole("ADMIN")
                )

                .addFilterBefore(adminFilter, BasicAuthenticationFilter.class);

        return http.build();
    }
}
