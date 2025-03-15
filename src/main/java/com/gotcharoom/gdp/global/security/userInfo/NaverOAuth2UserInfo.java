package com.gotcharoom.gdp.global.security.userInfo;

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

    // 공통화된 response 객체 반환 메서드
    private Map<String, Object> getResponse() {
        return (Map<String, Object>) attributes.get(usernameAttributeName);
    }

    @Override
    public String getId() {
        Map<String, Object> response = getResponse();
        return response != null ? (String) response.get("id") : null;
    }

    @Override
    public String getNickname() {
        Map<String, Object> response = getResponse();
        return response != null ? (String) response.get("nickname") : null;
    }

    @Override
    public String getName() {
        Map<String, Object> response = getResponse();
        return response != null ? (String) response.get("name") : null;
    }

    @Override
    public String getEmail() {
        Map<String, Object> response = getResponse();
        return response != null ? (String) response.get("email") : null;
    }

    @Override
    public String getImageUrl() {
        Map<String, Object> response = getResponse();
        return response != null ? (String) response.get("profile_image") : null;
    }
}
