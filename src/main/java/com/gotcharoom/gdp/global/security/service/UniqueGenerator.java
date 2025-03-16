package com.gotcharoom.gdp.global.security.service;

import com.gotcharoom.gdp.global.security.model.SocialType;
import com.gotcharoom.gdp.user.repository.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

@Service
public class UniqueGenerator {

    private final UserRepository userRepository;

    public UniqueGenerator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String generateUniqueId(SocialType socialType) {
        String id;
        do {
            id = socialType.name() + "-" + RandomStringUtils.randomAlphanumeric(12); // 예: google-A1B2C3d4e5f6
        } while (userRepository.findById(id).isPresent()); // 중복이면 다시 생성

        return id;
    }

    public String generateUniqueNickname() {
        String nickname;
        do {
            nickname = "User" + RandomStringUtils.randomAlphanumeric(6); // 예: UserA1B2C3
        } while (userRepository.findByNickName(nickname).isPresent()); // 중복이면 다시 생성

        return nickname;
    }
}
