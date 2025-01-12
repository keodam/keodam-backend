package com.keodam.keodam_backend.oauth.domain.userinfo;

import lombok.extern.slf4j.Slf4j;
import java.util.Map;

@Slf4j
public class GoogleUserInfo extends UserInfo {

    public GoogleUserInfo(Map<String, Object> attributes) { super(attributes); log.info(attributes.toString()); }

    @Override
    public String getId(){ return (String) attributes.get("sub"); }

    @Override
    public String getNickname(){ return (String) attributes.get("name"); }

    @Override
    public String getImageUrl(){ return (String) attributes.get("picture"); }

    @Override
    public String getEmail(){ return (String) attributes.get("email"); }
}
