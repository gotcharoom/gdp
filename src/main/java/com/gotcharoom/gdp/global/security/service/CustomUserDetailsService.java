package com.gotcharoom.gdp.global.security.service;

import com.gotcharoom.gdp.global.security.model.SocialType;
import com.gotcharoom.gdp.user.entity.GdpUser;
import com.gotcharoom.gdp.user.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;


    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        GdpUser gdpUser = userRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();

        String password = gdpUser.getPassword();

        if (gdpUser.getSocialType() != SocialType.GDP) {
            password = UUID.randomUUID().toString();
        }

        // TODO. [TR-YOO] authorities 논리 구현하기
        return User.builder()
                .username(gdpUser.getId())
                .password(password)
                .authorities(grantedAuthorities)
                .build();
    }
}
