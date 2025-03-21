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
        log.info("OAuth2 로그인 요청 처리 시작");

        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        log.info("OAuth2 기본 사용자 정보 로드 완료");

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        SocialType socialType = oAuth2Util.getSocialType(registrationId);
        log.info("소셜 타입 매핑 완료 - registrationId: {}, socialType: {}", registrationId, socialType);

        Map<String, Object> attributes = oAuth2User.getAttributes();
        log.info("OAuth2 사용자 Attributes 수신 - keys: {}", attributes.keySet());

        OAuth2Attributes extractAttributes = OAuth2Attributes.of(socialType, attributes);
        log.info("OAuth2Attributes 생성 완료 - name: {}", extractAttributes.getOauth2UserInfo().getName());

        GdpUser gdpUser = getUser(extractAttributes, socialType);

        if (gdpUser != null) {
            log.info("기존 사용자 조회 성공 - ID: {}", gdpUser.getId());
        } else {
            log.info("기존 사용자 없음, 신규 사용자 등록 시도");
            gdpUser = registerUser(extractAttributes, socialType);
            log.info("신규 사용자 등록 완료 - ID: {}", gdpUser.getId());
        }

        log.info("CustomOAuth2User 생성 및 반환");

        return new CustomOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(gdpUser.getRole().getKey())),
                attributes,
                extractAttributes.getUsernameAttributeKey(),
                gdpUser.getRole()
        );
    }

    private GdpUser getUser(OAuth2Attributes attributes, SocialType socialType) {
        String socialId = attributes.getOauth2UserInfo().getSocialId();
        log.info("기존 사용자 조회 시도 - socialType: {}, socialId: {}", socialType, socialId);

        return userRepository.findBySocialTypeAndSocialId(socialType, socialId).orElse(null);
    }

    private GdpUser registerUser(OAuth2Attributes attributes, SocialType socialType) {
        log.info("신규 사용자 등록 시작");

        GdpUser createdUser = attributes.toEntity(socialType, attributes.getOauth2UserInfo(), uniqueGenerator);
        userRepository.save(createdUser);

        log.info("신규 사용자 저장 완료 - ID: {}", createdUser.getId());
        return createdUser;
    }
}
