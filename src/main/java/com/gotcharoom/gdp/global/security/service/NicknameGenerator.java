package com.gotcharoom.gdp.global.security.service;

import com.gotcharoom.gdp.user.repository.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

@Service
public class NicknameGenerator {

    private final UserRepository userRepository;

    public NicknameGenerator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String generateUniqueNickname() {
        String nickname;
        do {
            nickname = "User" + RandomStringUtils.randomAlphanumeric(6); // 예: UserA1B2C3
        } while (userRepository.findByNickName(nickname).isPresent()); // 중복이면 다시 생성

        return nickname;
    }
}
