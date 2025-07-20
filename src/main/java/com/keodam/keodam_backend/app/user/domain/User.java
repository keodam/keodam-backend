package com.keodam.keodam_backend.app.user.domain;

import com.keodam.keodam_backend.exception.GeneralException;
import com.keodam.keodam_backend.global.code.status.ErrorStatus;
import com.keodam.keodam_backend.mypage.certification.domain.UserVerification;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Table(name = "user")
@Entity
@Getter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "profile_url")
    private String profileUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_type")
    private RoleType roleType;

    @Column(name = "oauth_id")
    private String oauthId;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Enumerated(EnumType.STRING)
    @Column(name = "social_type")
    private SocialType socialType;

    @Column(name = "roulette_coupon")
    private Integer rouletteCoupon;

    @Column(name = "coffee_coupon")
    private Integer coffeeCoupon;

    @Column(name = "badge")
    private String badge;

    @Column(name = "nickname_changed")
    private Boolean nicknameChanged;

    @Column(name = "nickname_changed_at")
    private LocalDateTime nicknameChangedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserVerification> verifications = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "profile_status")
    private ProfileStatus profileStatus;

    @Builder
    public User(String nickname, String email, String profileUrl, SocialType socialType, RoleType roleType, String oauthId) {
        this.nickname = nickname;
        this.email = email;
        this.profileUrl = profileUrl;
        this.roleType = roleType;
        this.socialType = socialType;
        this.oauthId = oauthId;
        this.password = UUID.randomUUID().toString();
        this.coffeeCoupon = 0;
        this.rouletteCoupon = 0;
    }

    public boolean canChangeNickname() {
        if (this.nickname == null || this.nicknameChangedAt == null) return true;
        return LocalDateTime.now().isAfter(this.nicknameChangedAt.plusDays(30));
    }

    public void updateNickname(String newNickname) {
        if (this.nickname != null && !canChangeNickname()) {
            throw new GeneralException(ErrorStatus.NICKNAME_ALREADY_CHANGED);
        }
        this.nickname = newNickname;
        this.nicknameChangedAt = LocalDateTime.now();
        this.nicknameChanged = true;
    }

    public void updateProfileImage(String newProfileImage) {
        this.profileUrl = newProfileImage;
        this.profileStatus = ProfileStatus.PENDING;
    }

    public void updateRole(RoleType roleType) {
        if (roleType != null) this.roleType = roleType;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}