package com.keodam.keodam_backend.oauth.domain.userinfo;

import java.util.Map;

public class KakaoUserInfo extends UserInfo{


    private Map<String, Object> profile = null;
    private Map<String, Object> account = null;
    public KakaoUserInfo(Map<String, Object> attributes) {
        super(attributes);
        //this.account = (Map<String, Object>) this.attributes.get("kakao_account");
        //this.profile = (Map<String, Object>) this.attributes.get("profile");
    }

    @Override
    public String getId(){ return String.valueOf(attributes.get("id")); }

    @Override
    public String getNickname(){ if(isProfileNull()) return null; return  (String)profile.get("name"); }

    @Override
    public String getImageUrl(){ if(isProfileNull()) return null; return (String) attributes.get("thumbnail_image_url"); }

    @Override
    public String getEmail(){ if(isAccountNull()) return null; return (String) account.get("email"); }

    private boolean isAccountNull(){ return attributes == null;}

    private boolean isProfileNull(){ return profile == null;}
}
