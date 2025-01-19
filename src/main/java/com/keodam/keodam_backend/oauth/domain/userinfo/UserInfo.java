package com.keodam.keodam_backend.oauth.domain.userinfo;

import java.util.Map;

public abstract class UserInfo {

    Map<String, Object> attributes;

    public UserInfo(Map<String, Object> attributes){ this.attributes = attributes; }

    public abstract String getId();
    public abstract String getEmail();
}
