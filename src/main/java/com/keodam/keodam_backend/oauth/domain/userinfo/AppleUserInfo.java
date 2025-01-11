package com.keodam.keodam_backend.oauth.domain.userinfo;

import java.util.Map;

public class AppleUserInfo extends UserInfo {

    public AppleUserInfo(Map<String, Object> attributes) { super(attributes); }

    @Override
    public String getId(){ return (String) attributes.get("sub"); }

    @Override
    public String getNickname(){ return (String) attributes.get("name"); }

    @Override
    public String getImageUrl(){ return (String) attributes.get("picture"); }

    @Override
    public String getEmail(){ return (String) attributes.get("email"); }
}
