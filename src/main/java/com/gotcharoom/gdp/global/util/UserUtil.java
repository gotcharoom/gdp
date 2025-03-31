package com.gotcharoom.gdp.global.util;

import com.gotcharoom.gdp.user.entity.GdpUser;
import com.gotcharoom.gdp.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserUtil {

    private static final Logger logger = LoggerFactory.getLogger(UserUtil.class);

    private final UserRepository userRepository;

    public UserUtil(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public GdpUser getUserFromContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            logger.info("Authentication is null. Cannot extract user from context.");
            throw new RuntimeException("인증 정보가 존재하지 않습니다.");
        }

        if (!authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            logger.info("Authentication is not valid or is anonymous. Principal: {}", authentication.getPrincipal());
            throw new RuntimeException("유효하지 않은 인증입니다.");
        }

        String userId = authentication.getName();
        logger.info("Fetching user from context. UserId: {}", userId);

        return userRepository.findById(userId).orElseThrow(() -> {
            logger.info("User not found in repository. UserId: {}", userId);
            return new RuntimeException("사용자를 찾을 수 없습니다.");
        });
    }
}
