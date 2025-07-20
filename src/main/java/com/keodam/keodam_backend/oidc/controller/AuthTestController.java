package com.keodam.keodam_backend.oidc.controller;

import com.keodam.keodam_backend.app.domain.User;
import com.keodam.keodam_backend.app.domain.RoleType;
import com.keodam.keodam_backend.app.domain.SocialType;
import com.keodam.keodam_backend.app.user.repository.UserRepository;
import com.keodam.keodam_backend.global.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "User TEST Login(실제 환경서는 삭제예정)", description = "TEST Login API")
public class AuthTestController {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    @PostMapping("/test-login")
    @Operation(summary = "로그인 이후 테스트용 코드", description = "소셜 로그인 API BYPASS 목적의 임시 토큰발급")
    @Parameter(name = "email", description = "Bearer 토큰 발급을 위한 임시 이메일주소", required = true, in = ParameterIn.QUERY)
    public ResponseEntity<AuthTestControllerDto> testLogin(@RequestParam String email) {

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = User.builder()
                    .email(email)
                    .nickname("테스트유저")
                    .profileUrl(null)
                    .roleType(RoleType.GUEST)
                    .socialType(SocialType.GOOGLE)
                    .oauthId("TEST-" + UUID.randomUUID())
                    .build();
            return userRepository.save(newUser);
        });

        String accessToken = jwtService.createAccessToken(user.getEmail());
        String refreshToken = jwtService.createRefreshToken();

        user.updateRefreshToken(refreshToken);
        userRepository.save(user);

        AuthTestControllerDto response = new AuthTestControllerDto(accessToken, refreshToken);
        return ResponseEntity.ok(response);
    }
}