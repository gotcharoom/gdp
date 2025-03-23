package com.gotcharoom.gdp.global.security.service;

import com.gotcharoom.gdp.global.security.model.SocialType;
import com.gotcharoom.gdp.user.entity.GdpUser;
import com.gotcharoom.gdp.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        log.info("UserDetails 조회 요청 - 사용자 ID: {}", id);

        GdpUser gdpUser = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.info("사용자 정보를 찾을 수 없음 - ID: {}", id);
                    return new UsernameNotFoundException("User not found: " + id);
                });

        log.info("사용자 정보 조회 성공 - ID: {}, 소셜타입: {}", gdpUser.getId(), gdpUser.getSocialType());

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        // TODO. [TR-YOO] authorities 논리 구현하기

        String password = gdpUser.getPassword();
        if (gdpUser.getSocialType() != SocialType.GDP) {
            password = UUID.randomUUID().toString();
            log.info("소셜 로그인 유저로 임시 패스워드 생성");
        }

        log.info("UserDetails 생성 완료 - ID: {}", gdpUser.getId());

        return User.builder()
                .username(gdpUser.getId())
                .password(password)
                .authorities(grantedAuthorities)
                .build();
    }
}
