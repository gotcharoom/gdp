package com.gotcharoom.gdp.user.service;

import com.gotcharoom.gdp.global.security.model.SocialType;
import com.gotcharoom.gdp.global.util.FileUploadUtil;
import com.gotcharoom.gdp.upload.service.UploadFileService;
import com.gotcharoom.gdp.user.entity.GdpUser;
import com.gotcharoom.gdp.user.model.UserDetailsResponse;
import com.gotcharoom.gdp.user.model.UserDetailsUpdateRequest;
import com.gotcharoom.gdp.user.model.UserSignUpRequest;
import com.gotcharoom.gdp.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;

@Service
public class UserService {

    @Value("${gdp.file-server.url}")
    private String SERVER_URL;

    @Value("${gdp.file-server.dir.profile}")
    private String PROFILE_DIR;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final UploadFileService uploadFileService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UploadFileService uploadFileService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.uploadFileService = uploadFileService;
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
                .imageUrl(user.getImageUrl())
                .imageCropArea(user.getCropArea())
                .build();
    }

    @Transactional
    public void putUserDetails(UserDetailsUpdateRequest request) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            throw new RuntimeException();
        }

        GdpUser user = userRepository.findById(authentication.getName()).orElseThrow();

        String imageUrl = uploadFileService.uploadProfileFile(user.getImageUrl(), request.getImageFile());

        GdpUser updatedUser = user.updateProfile(request.getNickname(), request.getName(), request.getEmail(), imageUrl, request.getImageCropArea());
        userRepository.save(updatedUser);
    }
}
