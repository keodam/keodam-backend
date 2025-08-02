package com.keodam.keodam_backend.global.security;

import com.keodam.keodam_backend.app.user.domain.RoleType;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

@Builder
@Getter
public class JwtPrincipal {

    private final Long userId;
    private final String email;
    private final RoleType role;

    public JwtPrincipal(Long userId, String email, RoleType role) {
        this.userId = userId;
        this.email = email;
        this.role = role;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {

        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }
}