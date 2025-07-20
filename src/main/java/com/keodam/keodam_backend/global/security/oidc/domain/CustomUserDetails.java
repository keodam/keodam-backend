package com.keodam.keodam_backend.global.security.oidc.domain;

import com.keodam.keodam_backend.app.user.domain.RoleType;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import java.util.*;

@Getter
public class CustomUserDetails implements UserDetails, OidcUser {

    private final String username;
    private final String password;
    private final RoleType roleType;
    private final Set<GrantedAuthority> authorities;
    private final Map<String, Object> attributes;

    public CustomUserDetails(String username, RoleType roleType,
                             Collection<? extends GrantedAuthority> authorities,
                             Map<String, Object> attributes) {

        this.username = username;
        this.password = null;
        this.roleType = roleType;
        this.authorities = (authorities != null)
                ? Collections.unmodifiableSet(new LinkedHashSet<>(this.sortAuthorities(authorities)))
                : Collections.unmodifiableSet(new LinkedHashSet<>(AuthorityUtils.NO_AUTHORITIES));
        this.attributes = attributes;
    }

    public CustomUserDetails(String email, com.keodam.keodam_backend.app.user.domain.RoleType roleType, Set<SimpleGrantedAuthority> singleton, Map<String, Object> attributes,
                             String username, String password, RoleType roleType1, Set<GrantedAuthority> authorities,
                             Map<String, Object> attributes1) {
        this.username = username;
        this.password = password;
        this.roleType = roleType1;
        this.authorities = authorities;
        this.attributes = attributes1;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public String getName() {

        return username;
    }

    @Override
    public Map<String, Object> getClaims() {

        return this.attributes;
    }

    @Override
    public OidcUserInfo getUserInfo() {

        return null;
    }

    @Override
    public OidcIdToken getIdToken() {

        return null;
    }

    private Set<GrantedAuthority> sortAuthorities(Collection<? extends GrantedAuthority> authorities) {
        SortedSet<GrantedAuthority> sortedAuthorities = new TreeSet<>(
                Comparator.comparing(GrantedAuthority::getAuthority));
        sortedAuthorities.addAll(authorities);
        return sortedAuthorities;
    }
}
