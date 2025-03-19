package com.gotcharoom.gdp.user.service;

import com.gotcharoom.gdp.global.security.model.SocialType;
import com.gotcharoom.gdp.user.entity.GdpUser;
import com.gotcharoom.gdp.user.model.UserDetailsResponse;
import com.gotcharoom.gdp.user.model.UserDetailsUpdateRequest;
import com.gotcharoom.gdp.user.model.UserSignUpRequest;
import com.gotcharoom.gdp.user.repository.UserRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean registerUser(UserSignUpRequest userSignUpRequest) {
        try {
            String encodedPassword = passwordEncoder.encode(userSignUpRequest.getPassword());

            GdpUser gdpUser = userSignUpRequest.toEntity(encodedPassword);
            userRepository.save(gdpUser);

            return true;
        } catch(Exception e) {

            return false;
        }

    }

    public void deleteUser() {
    }

    public boolean checkDuplicateId(String id) {
        boolean isExist =userRepository.findById(id).isPresent();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return isExist;
        }

        GdpUser gdpUser = userRepository.findById(authentication.getName()).orElseThrow();
        String loginUserId = gdpUser.getId();
        boolean isCurrentId = id.equals(loginUserId);

        return !isCurrentId && isExist;
    }

    public boolean checkDuplicateNickname(String nickname) {
        boolean isExist =userRepository.findByNickname(nickname).isPresent();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return isExist;
        }

        GdpUser gdpUser = userRepository.findById(authentication.getName()).orElseThrow();
        String loginUserNickname = gdpUser.getNickname();
        boolean isCurrentNickname = nickname.equals(loginUserNickname);

        return !isCurrentNickname && isExist;
    }

    public boolean checkDuplicateEmail(String email) {
        boolean isExist =userRepository.findBySocialTypeAndEmail(SocialType.GDP, email).isPresent();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return isExist;
        }

        GdpUser gdpUser = userRepository.findById(authentication.getName()).orElseThrow();
        String loginUserEmail = gdpUser.getEmail();
        boolean isCurrentEmail = email.equals(loginUserEmail);

        return !isCurrentEmail && isExist;
    }

    public UserDetailsResponse getUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            throw new RuntimeException();
        }
        GdpUser user = userRepository.findById(authentication.getName()).orElseThrow();

        return UserDetailsResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .nickname(user.getNickname())
                .build();
    }

    public void putUserDetails(UserDetailsUpdateRequest request) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
//            throw new RuntimeException();
//        }
//        GdpUser user = userRepository.findById(authentication.getName()).orElseThrow();
        MultipartFile imageFile = request.getImageFile();
        if (imageFile != null && !imageFile.isEmpty()) {
            System.out.println("Received Image File: " + imageFile.getOriginalFilename());
            System.out.println("File Size: " + imageFile.getSize());
            System.out.println("File Type: " + imageFile.getContentType());
        } else {
            System.out.println("No image file received.");
        }

    }
}
