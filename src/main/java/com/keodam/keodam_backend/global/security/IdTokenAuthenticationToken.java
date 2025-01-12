package com.keodam.keodam_backend.global.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class IdTokenAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal; // 주로 사용자 정보 (e.g., 사용자 ID 또는 email)
    private final String idToken;  // 토큰 자체 (자격증명)
    private final String provider;

    // 인증 전: IdToken 기반으로 토큰 생성
    public IdTokenAuthenticationToken(String idToken, String provider) {
        super(null); // 권한은 인증 후에 설정됨
        this.idToken = idToken;
        this.principal = null; // 아직 인증되지 않았기 때문에 null
        this.provider = provider;
        setAuthenticated(false); // 인증 전 상태로 설정
    }

    // 인증 후: Principal과 Authorities 설정
    public IdTokenAuthenticationToken(Object principal, String email, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.idToken = null; // 인증 후에는 ID 토큰을 사용하지 않을 수도 있음
        this.provider = null;
        this.principal = principal;
        setAuthenticated(true); // 인증 후 상태로 설정
    }

    @Override
    public Object getCredentials() {
        return idToken; // 자격증명 반환
    }

    @Override
    public Object getPrincipal() {
        return principal; // 사용자 정보 반환
    }
}
