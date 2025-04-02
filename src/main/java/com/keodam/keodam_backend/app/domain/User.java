package com.keodam.keodam_backend.app.domain;

import com.keodam.keodam_backend.mypage.domain.UserVerification;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Table(name = "user")
@Entity
@Getter
@NoArgsConstructor
public class User{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="nickname")
    private String nickname;
    @Column(name="password")
    private String password;
    @Column(name="email")
    private String email;
    @Column(name="profile_url")
    private String profileUrl;
    @Enumerated(EnumType.STRING)
    @Column(name="role_type")
    private RoleType roleType;
    @Column(name="oauth_id")
    private String oAuthId;
    @Column(name="refresh_token")
    private String refreshToken;
    @Enumerated(EnumType.STRING)
    @Column(name="social_type")
    private SocialType socialType;
    @Column(name="roulette_coupon")
    private Integer rouletteCoupon;
    @Column(name="coffee_coupon")
    private Integer coffeeCoupon;
    @Column(name="badge")
    private String badge;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserVerification> verifications = new ArrayList<>();

    @Builder
    public User(String nickname, String email, String profileUrl, SocialType socialType, RoleType roleType, String oAuthId) {
        this.nickname = nickname;
        this.email = email;
        this.profileUrl = profileUrl;
        this.roleType = roleType;
        this.socialType = socialType;
        this.oAuthId = oAuthId;
        this.password = UUID.randomUUID().toString();
        this.coffeeCoupon = 0;
        this.rouletteCoupon = 0;
    }

    public void updateRefreshToken(String refreshToken){
        this.refreshToken = refreshToken;
    }
}
