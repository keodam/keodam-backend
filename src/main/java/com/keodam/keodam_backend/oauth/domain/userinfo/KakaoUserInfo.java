package com.keodam.keodam_backend.oauth.domain.userinfo;
import java.util.Map;

public class KakaoUserInfo extends UserInfo{

    public KakaoUserInfo(Map<String, Object> attributes) { super(attributes); }

    @Override
    public String getId(){ return String.valueOf(attributes.get("sub")); }

    @Override
    public String getNickname(){ return (String) attributes.get("nickname"); }

    @Override
    public String getImageUrl(){ return (String) attributes.get("thumbnail_image_url"); }

    @Override
    public String getEmail(){ return (String) attributes.get("email"); }

}
