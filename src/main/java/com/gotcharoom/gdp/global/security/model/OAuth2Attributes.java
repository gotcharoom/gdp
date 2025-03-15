package com.gotcharoom.gdp.global.security.model;

import com.gotcharoom.gdp.global.security.userInfo.*;
import com.gotcharoom.gdp.user.entity.GdpUser;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
public class OAuth2Attributes {

    private final String usernameAttributeKey; // OAuth2 로그인 진행 시 키가 되는 필드 값, PK와 같은 의미
    private final OAuth2UserInfo oauth2UserInfo; // 소셜 타입별 로그인 유저 정보(닉네임, 이메일, 프로필 사진 등등)

    @Builder
    private OAuth2Attributes(String usernameAttributeKey, OAuth2UserInfo oauth2UserInfo) {
        this.usernameAttributeKey = usernameAttributeKey;
        this.oauth2UserInfo = oauth2UserInfo;
    }

    public static OAuth2Attributes of(SocialType socialType, Map<String, Object> attributes) {

        if(socialType == SocialType.GDP) {
            return ofGdp(attributes);
        }
        if (socialType == SocialType.NAVER) {
            return ofNaver(attributes);
        }
        if (socialType == SocialType.KAKAO) {
            return ofKakao(attributes);
        }

        return ofGoogle(attributes);
    }

    public static OAuth2Attributes ofGdp(Map<String, Object> attributes) {
        return OAuth2Attributes.builder()
                .usernameAttributeKey(GdpOAuth2UserInfo.usernameAttributeName)
                .oauth2UserInfo(new GdpOAuth2UserInfo(attributes))
                .build();
    }

    public static OAuth2Attributes ofNaver(Map<String, Object> attributes) {
        return OAuth2Attributes.builder()
                .usernameAttributeKey(NaverOAuth2UserInfo.usernameAttributeName)
                .oauth2UserInfo(new NaverOAuth2UserInfo(attributes))
                .build();
    }

    private static OAuth2Attributes ofKakao(Map<String, Object> attributes) {
        return OAuth2Attributes.builder()
                .usernameAttributeKey(KakaoOAuth2UserInfo.usernameAttributeName)
                .oauth2UserInfo(new KakaoOAuth2UserInfo(attributes))
                .build();
    }

    public static OAuth2Attributes ofGoogle(Map<String, Object> attributes) {
        return OAuth2Attributes.builder()
                .usernameAttributeKey(GoogleOAuth2UserInfo.usernameAttributeName)
                .oauth2UserInfo(new GoogleOAuth2UserInfo(attributes))
                .build();
    }

    // TODO. [TR-YOO] Role 추가하기
    public GdpUser toEntity(SocialType socialType, OAuth2UserInfo oauth2UserInfo) {
        return GdpUser.builder()
                .socialType(socialType)
                .id(oauth2UserInfo.getId())
                .socialId(oauth2UserInfo.getId())
                .nickName(oauth2UserInfo.getNickname())
                .name(oauth2UserInfo.getName())
                .email(oauth2UserInfo.getEmail())
                .imageUrl(oauth2UserInfo.getImageUrl())
                .role(Role.USER)
                .build();
    }
}
