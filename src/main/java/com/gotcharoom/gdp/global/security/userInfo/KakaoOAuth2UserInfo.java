package com.gotcharoom.gdp.global.security.userInfo;

import java.util.Map;

public class KakaoOAuth2UserInfo extends OAuth2UserInfo {

    public static final String usernameAttributeName = "id";

    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getUsernameAttributeKey() {
        return usernameAttributeName;
    }

    @Override
    public String getId() {
        return String.valueOf(attributes.get(usernameAttributeName));
    }

    // 공통 메서드: kakao_account 가져오기
    private Map<String, Object> getKakaoAccount() {
        return (Map<String, Object>) attributes.get("kakao_account");
    }

    // 공통 메서드: profile 가져오기
    private Map<String, Object> getProfile() {
        Map<String, Object> account = getKakaoAccount();
        return account != null ? (Map<String, Object>) account.get("profile") : null;
    }

    @Override
    public String getName() {
        Map<String, Object> profile = getProfile();
        return profile != null ? (String) profile.get("nickname") : null;
    }

    @Override
    public String getEmail() {
        Map<String, Object> account = getKakaoAccount();
        return account != null ? (String) account.get("email") : null;
    }

    @Override
    public String getImageUrl() {
        Map<String, Object> profile = getProfile();
        return profile != null ? (String) profile.get("thumbnail_image_url") : null;
    }
}
