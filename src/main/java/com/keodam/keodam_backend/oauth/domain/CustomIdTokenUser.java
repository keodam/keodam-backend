package com.keodam.keodam_backend.oauth.domain;

import com.keodam.keodam_backend.app.domain.RoleType;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import javax.swing.*;
import java.util.*;

@Getter
public class CustomIdTokenUser implements UserDetails {

    private String username;
    private String password;
    private String email;
    private RoleType roleType;
    private final Set<GrantedAuthority> authorities;

    public CustomIdTokenUser(Collection<? extends GrantedAuthority> authorities,
                             String username, String password,
                             String email, RoleType roleType) {
        this.authorities = (authorities != null)
                ? Collections.unmodifiableSet(new LinkedHashSet<>(this.sortAuthorities(authorities)))
                : Collections.unmodifiableSet(new LinkedHashSet<>(AuthorityUtils.NO_AUTHORITIES));
        this.username = username;
        this.password = password;
        this.email = email;
        this.roleType = roleType;

    }

    private Set<GrantedAuthority> sortAuthorities(Collection<? extends GrantedAuthority> authorities) {
        SortedSet<GrantedAuthority> sortedAuthorities = new TreeSet<>(
                Comparator.comparing(GrantedAuthority::getAuthority));
        sortedAuthorities.addAll(authorities);
        return sortedAuthorities;
    }
}
