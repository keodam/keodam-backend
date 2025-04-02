package com.keodam.keodam_backend.oauth.domain;

import com.keodam.keodam_backend.app.domain.RoleType;
import com.keodam.keodam_backend.app.domain.SocialType;
import com.keodam.keodam_backend.app.user.domain.User;
import com.keodam.keodam_backend.oauth.domain.userinfo.AppleUserInfo;
import com.keodam.keodam_backend.oauth.domain.userinfo.GoogleUserInfo;
import com.keodam.keodam_backend.oauth.domain.userinfo.KakaoUserInfo;
import com.keodam.keodam_backend.oauth.domain.userinfo.UserInfo;
import lombok.Getter;

import java.util.Map;

@Getter
public class IdTokenAttributes {

    private UserInfo userInfo;
    private SocialType socialType;

    public IdTokenAttributes(Map<String, Object> attributes, SocialType socialType){
        this.socialType = socialType;
        if(socialType == SocialType.GOOGLE) this.userInfo = new GoogleUserInfo(attributes);
        if(socialType == SocialType.KAKAO) this.userInfo = new KakaoUserInfo(attributes);
        if(socialType == SocialType.APPLE) this.userInfo = new AppleUserInfo(attributes);
    }

    public User toUser() {
        return User.builder()
                .socialType(socialType)
                .oauthId(userInfo.getId())
                .nickname("")
                .profileUrl(null)
                .email(userInfo.getEmail())
                .roleType(RoleType.GUEST)
                .build();
    }
}
