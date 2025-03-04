package com.gotcharoom.gdp.global.security;

import java.util.Map;

public class NaverOAuth2UserInfo extends OAuth2UserInfo {

    public static final String usernameAttributeName = "response";

    public NaverOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getUsernameAttributeKey() {
        return usernameAttributeName;
    }

    @Override
    public String getId() {
        Map<String, Object> response = (Map<String, Object>) attributes.get(usernameAttributeName);

        if (response == null) {
            return null;
        }

        return (String) response.get("id");
    }

    @Override
    public String getNickname() {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        if (response == null) {
            return null;
        }

        return (String) response.get("nickname");
    }

    @Override
    public String getImageUrl() {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        if (response == null) {
            return null;
        }

        return (String) response.get("profile_image");
    }
}

