package com.gotcharoom.gdp.global.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gotcharoom.gdp.global.security.OAuth2Attributes;
import com.gotcharoom.gdp.global.security.SocialType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OAuth2Util {

    private static final String GDP = "GDP";
    private static final String GOOGLE = "google";
    private static final String NAVER = "naver";
    private static final String KAKAO = "kakao";

    public String getSocialIdFromAuthentication(Authentication authentication) throws JsonProcessingException {
        String subject = authentication.getName();

        if(authentication.getPrincipal() instanceof OAuth2User oAuth2User) {
            Map<String, Object> attributes = oAuth2User.getAttributes();

            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            String registrationId = oauthToken.getAuthorizedClientRegistrationId();
            SocialType socialType = getSocialType(registrationId);

            OAuth2Attributes extractAttributes = OAuth2Attributes.of(socialType, attributes);

            return extractAttributes.getOauth2UserInfo().getId();
        }

        return subject;
    }

    public SocialType getSocialType(String registrationId) {
        if(GOOGLE.equals(registrationId)) {
            return SocialType.GOOGLE;
        }
        if(NAVER.equals(registrationId)) {
            return SocialType.NAVER;
        }
        if(KAKAO.equals(registrationId)) {
            return SocialType.KAKAO;
        }
        return SocialType.GDP;
    }
}
