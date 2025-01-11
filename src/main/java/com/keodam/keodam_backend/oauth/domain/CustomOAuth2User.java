package com.keodam.keodam_backend.oauth.domain;

import com.keodam.keodam_backend.app.domain.RoleType;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter

public class CustomOAuth2User extends DefaultOAuth2User {

    private String email;
    private com.keodam.keodam_backend.app.domain.RoleType roleType;

    @Builder
    public CustomOAuth2User(Collection<? extends GrantedAuthority> authorities,
                            Map<String, Object> attributes, String nameAttributeKey, String email, RoleType roleType) {
        super(authorities, attributes, nameAttributeKey);
        this.email = email;
        this.roleType = roleType;
    }
}