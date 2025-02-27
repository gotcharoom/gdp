package com.gotcharoom.gdp.user.service;

import com.gotcharoom.gdp.user.entity.GdpUser;
import com.gotcharoom.gdp.user.model.UserSignUpRequest;
import com.gotcharoom.gdp.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(UserSignUpRequest userSignUpRequest) {
        String encodedPassword = passwordEncoder.encode(userSignUpRequest.getPassword());

        GdpUser gdpUser = userSignUpRequest.toEntity(encodedPassword);
        userRepository.save(gdpUser);
    }

    public void deleteUser() {
    }


}
