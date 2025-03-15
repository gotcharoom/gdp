package com.gotcharoom.gdp.global.security.userInfo;

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
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getImageUrl() {
        return (String) attributes.get("picture");
    }
}