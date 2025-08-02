package com.keodam.keodam_backend.oidc.domain;

import com.keodam.keodam_backend.app.user.domain.RoleType;
import com.keodam.keodam_backend.app.user.domain.SocialType;
import com.keodam.keodam_backend.app.user.domain.User;
import lombok.Getter;

import java.util.Map;

@Getter
public class IdTokenAttributes {

    private UserInfo userInfo;
    private SocialType socialType;

    public IdTokenAttributes(Map<String, Object> attributes, SocialType socialType){

       userInfo = new UserInfo(attributes);
       this.socialType = socialType;
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
