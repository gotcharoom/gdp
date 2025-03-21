package com.gotcharoom.gdp.global.security.service;

import com.gotcharoom.gdp.global.security.model.OAuth2Attributes;
import com.gotcharoom.gdp.global.security.model.CustomOAuth2User;
import com.gotcharoom.gdp.global.security.model.SocialType;
import com.gotcharoom.gdp.global.util.OAuth2Util;
import com.gotcharoom.gdp.global.util.UniqueGenerator;
import com.gotcharoom.gdp.user.entity.GdpUser;
import com.gotcharoom.gdp.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Service
public class CustomOauth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final OAuth2Util oAuth2Util;
    private final UserRepository userRepository;

    private final UniqueGenerator uniqueGenerator;

    public CustomOauth2UserService(
            OAuth2Util oAuth2Util,
            UserRepository userRepository,
            UniqueGenerator uniqueGenerator
    ) {
        this.oAuth2Util = oAuth2Util;
        this.userRepository = userRepository;
        this.uniqueGenerator = uniqueGenerator;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("CustomOAuth2UserService.loadUser() 실행 - OAuth2 로그인 요청 진입");

        // 전달받은 기본 User 객체 가져오기
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        //registrationId로 저장되어 있는 Social Type을 가져와서 Enum으로 변환
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        SocialType socialType = oAuth2Util.getSocialType(registrationId);

        // OAuth2 로그인 시 키(PK)가 되는 값
//        String userNameAttributeName = userRequest.getClientRegistration()
//                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        // 기본 User 객체를 Key, Value의 Map으로 가져오기
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // socialType에 따라 유저 정보를 통해 OAuthAttributes 객체 생성
        OAuth2Attributes extractAttributes = OAuth2Attributes.of(socialType, attributes);

        // Social 정보로 User 객체 가져오기 (없다면 생성됨)
        GdpUser gdpUser = getUser(extractAttributes, socialType);

        if(gdpUser == null) {
            gdpUser = registerUser(extractAttributes, socialType);
        }

        return new CustomOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(gdpUser.getRole().getKey())),
                attributes,
                extractAttributes.getUsernameAttributeKey(),
                gdpUser.getRole()
        );
    }



    private GdpUser getUser(OAuth2Attributes attributes, SocialType socialType) {
        return userRepository.findBySocialTypeAndId(socialType,
                attributes.getOauth2UserInfo().getId()).orElse(null);
    }

    private GdpUser registerUser(OAuth2Attributes attributes, SocialType socialType) {
        GdpUser createdUser = attributes.toEntity(socialType, attributes.getOauth2UserInfo(), uniqueGenerator);
        userRepository.save(createdUser);

        return createdUser;
    }
}
