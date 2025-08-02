package com.keodam.keodam_backend.oidc.domain;

import java.util.Map;

public class UserInfo {

    Map<String, Object> attributes;

    public UserInfo(Map<String, Object> attributes){ this.attributes = attributes; }

    public String getId(){ return (String) attributes.get("sub"); }

    public String getEmail(){ return (String) attributes.get("email"); }
}
