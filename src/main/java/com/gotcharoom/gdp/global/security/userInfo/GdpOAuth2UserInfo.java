package com.gotcharoom.gdp.global.security.userInfo;

import java.util.Map;

public class GdpOAuth2UserInfo extends OAuth2UserInfo {

    public static final String usernameAttributeName = "id";

    public GdpOAuth2UserInfo(Map<String, Object> attributes) {
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
        return (String) attributes.get("nickName");
    }

    @Override
    public String getImageUrl() {
        return (String) attributes.get("imageUrl");
    }
}
