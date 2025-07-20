package com.keodam.keodam_backend.global.security.oidc;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class IdTokenAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal; // 인가 코드가 여기에 담김

    public IdTokenAuthenticationToken(Object principal) {
        super(null); // 권한은 초기에는 없음
        this.principal = principal;
        setAuthenticated(false); // 아직 인증되지 않음
    }

    @Override
    public Object getCredentials() {
        return null; // 자격 증명 (비밀번호 등)은 없음
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }
}