package com.keodam.keodam_backend.oauth.domain;

import com.keodam.keodam_backend.app.domain.RoleType;
import com.keodam.keodam_backend.app.domain.SocialType;
import com.keodam.keodam_backend.app.domain.User;
import com.keodam.keodam_backend.oauth.domain.userinfo.AppleUserInfo;
import com.keodam.keodam_backend.oauth.domain.userinfo.GoogleUserInfo;
import com.keodam.keodam_backend.oauth.domain.userinfo.KakaoUserInfo;
import com.keodam.keodam_backend.oauth.domain.userinfo.UserInfo;
import lombok.Getter;

import java.util.Map;

@Getter
public class OAuthAttributes {

    private String nameAttributeKey;
    private UserInfo userInfo;
    private SocialType socialType;

    public OAuthAttributes(SocialType socialType, String nameAttributeKey, Map<String, Object> attributes) {
        this.nameAttributeKey = nameAttributeKey;
        this.socialType = socialType;

        if(socialType == SocialType.GOOGLE) this.userInfo = new GoogleUserInfo(attributes);
        if(socialType == SocialType.KAKAO) this.userInfo = new KakaoUserInfo(attributes);
        if(socialType == SocialType.APPLE) this.userInfo = new AppleUserInfo(attributes);
    }

    public User toEntity() {
        return User.builder()
                .socialType(socialType)
                .oAuthId(userInfo.getId())
                .nickname(userInfo.getNickname())
                .profileUrl(userInfo.getImageUrl())
                .email(userInfo.getEmail())
                .roleType(RoleType.GUEST)
                .build();
    }
}
