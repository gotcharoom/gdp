package com.gotcharoom.gdp.global.security;

import java.util.Map;

public class GoogleOAuth2UserInfo extends OAuth2UserInfo {

    public static final String usernameAttributeName = "sub";

    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getUsernameAttributeKey() {
        return usernameAttributeName;
    }

    @Override
    public String getId() {
        return (String) attributes.get(usernameAttributeName);
    }

    @Override
    public String getNickname() {
        return (String) attributes.get("name");
    }

    @Override
    public String getImageUrl() {
        return (String) attributes.get("picture");
    }
}