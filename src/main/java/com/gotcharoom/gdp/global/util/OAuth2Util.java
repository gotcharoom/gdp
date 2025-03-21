package com.gotcharoom.gdp.global.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gotcharoom.gdp.global.security.model.OAuth2Attributes;
import com.gotcharoom.gdp.global.security.model.SocialType;
import com.gotcharoom.gdp.user.entity.GdpUser;
import com.gotcharoom.gdp.user.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OAuth2Util {

    private static final String GDP = "GDP";
    private static final String GOOGLE = "GOOGLE";
    private static final String NAVER = "NAVER";
    private static final String KAKAO = "KAKAO";

    public final UserRepository userRepository;

    public OAuth2Util(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String getIdFromAuthentication(Authentication authentication) throws JsonProcessingException {
        String subject = authentication.getName();

        // OAuth 2 User의 Token의 subject 설정하는 부분
        if(authentication.getPrincipal() instanceof OAuth2User oAuth2User) {
            return getIdFromOAuth2User(oAuth2User, authentication);
        }

        return subject;
    }

    private String getIdFromOAuth2User(OAuth2User oAuth2User, Authentication authentication) throws JsonProcessingException {
        Map<String, Object> attributes = oAuth2User.getAttributes();

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        String registrationId = oauthToken.getAuthorizedClientRegistrationId();
        SocialType socialType = getSocialType(registrationId);

        OAuth2Attributes extractAttributes = OAuth2Attributes.of(socialType, attributes);

        String socialId = extractAttributes.getOauth2UserInfo().getSocialId();
        GdpUser gdpUser = userRepository.findBySocialTypeAndSocialId(socialType, socialId)
                .orElseThrow(() -> new BadCredentialsException(
                        String.format("Invalid credentials for SocialType: %s, SocialId: %s", socialType, socialId)
                ));
        return gdpUser.getId();
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
