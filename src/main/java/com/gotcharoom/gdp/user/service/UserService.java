package com.gotcharoom.gdp.user.service;

import com.gotcharoom.gdp.global.api.ErrorResponse;
import com.gotcharoom.gdp.global.exception.custom.ChangePasswordException;
import com.gotcharoom.gdp.global.security.model.SocialType;
import com.gotcharoom.gdp.global.util.FileUploadUtil;
import com.gotcharoom.gdp.global.util.UserUtil;
import com.gotcharoom.gdp.platform.service.PlatformService;
import com.gotcharoom.gdp.upload.service.UploadFileService;
import com.gotcharoom.gdp.user.entity.GdpUser;
import com.gotcharoom.gdp.user.model.*;
import com.gotcharoom.gdp.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UploadFileService uploadFileService;
    private final PlatformService platformService;
    private final UserUtil userUtil;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            UploadFileService uploadFileService,
            PlatformService platformService,
            UserUtil userUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.uploadFileService = uploadFileService;
        this.platformService = platformService;
        this.userUtil = userUtil;
    }

    public boolean registerUser(UserSignUpRequest userSignUpRequest) {
        log.info("회원가입 요청 수신 - ID: {}", userSignUpRequest.getId());
        try {
            String encodedPassword = passwordEncoder.encode(userSignUpRequest.getPassword());
            GdpUser gdpUser = userSignUpRequest.toEntity(encodedPassword);
            userRepository.save(gdpUser);
            log.info("회원가입 성공 - ID: {}", gdpUser.getId());
            return true;
        } catch (Exception e) {
            log.info("회원가입 실패 - ID: {}, 메시지: {}", userSignUpRequest.getId(), e.getMessage());
            return false;
        }
    }

    public void deleteUser() {
        // 추후 구현
        log.info("회원 탈퇴 요청 수신 - 구현되지 않음");
    }

    public boolean checkDuplicateId(String id) {
        log.info("ID 중복 확인 요청 - ID: {}", id);
        boolean isExist = userRepository.findById(id).isPresent();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return isExist;
        }

        GdpUser gdpUser = userRepository.findById(authentication.getName()).orElseThrow();
        boolean isCurrentId = id.equals(gdpUser.getId());
        return !isCurrentId && isExist;
    }

    public boolean checkDuplicateNickname(String nickname) {
        log.info("닉네임 중복 확인 요청 - 닉네임: {}", nickname);
        boolean isExist = userRepository.findByNickname(nickname).isPresent();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return isExist;
        }

        GdpUser gdpUser = userRepository.findById(authentication.getName()).orElseThrow();
        boolean isCurrentNickname = nickname.equals(gdpUser.getNickname());
        return !isCurrentNickname && isExist;
    }

    public boolean checkDuplicateEmail(String email) {
        log.info("이메일 중복 확인 요청 - 이메일: {}", email);
        boolean isExist = userRepository.findBySocialTypeAndEmail(SocialType.GDP, email).isPresent();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return isExist;
        }

        GdpUser gdpUser = userRepository.findById(authentication.getName()).orElseThrow();
        boolean isCurrentEmail = email.equals(gdpUser.getEmail());
        return !isCurrentEmail && isExist;
    }

    public UserDetailsResponse getUserDetails() {
        log.info("회원 정보 조회 요청 수신");
        GdpUser user = userUtil.getUserFromContext();

        List<UserDetailPlatform> userDetailPlatformList = platformService.getUserDetailPlatforms(user.getUid());

        return UserDetailsResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .nickname(user.getNickname())
                .imageUrl(user.getImageUrl())
                .imageCropArea(user.getCropArea())
                .platforms(userDetailPlatformList)
                .build();
    }

    @Transactional
    public void putUserDetails(UserDetailsUpdateRequest request) throws IOException {
        log.info("회원 정보 수정 요청 수신");
        GdpUser user = userUtil.getUserFromContext();

        String imageUrl = uploadFileService.uploadProfileFile(user.getImageUrl(), request.getImageFile());
        log.info("프로필 이미지 업로드 완료 - URL: {}", imageUrl);

        GdpUser updatedUser = user.updateProfile(
                request.getNickname(),
                request.getName(),
                request.getEmail(),
                imageUrl,
                request.getImageCropArea()
        );
        userRepository.save(updatedUser);
        log.info("회원 정보 수정 완료 - ID: {}", updatedUser.getId());
    }

    public void putUserPassword(UserPasswordUpdateRequest request) {
        log.info("비밀번호 변경 요청 수신");
        GdpUser user = userUtil.getUserFromContext();

        if (!SocialType.GDP.equals(user.getSocialType())) {
            log.info("비밀번호 변경 불가 - 소셜 로그인 유저: {}", user.getSocialType());
            throw new ChangePasswordException(ErrorResponse.PASSWORD_CHANGE_GDP_USER_ONLY);
        }

        if (!passwordEncoder.matches(request.getPrevPassword(), user.getPassword())) {
            log.info("현재 비밀번호 불일치 - 사용자 ID: {}", user.getId());
            throw new ChangePasswordException(ErrorResponse.PASSWORD_CHANGE_NOT_CORRESPOND_CURRENT_PASSWORD);
        }

        if (!request.getNewPassword().equals(request.getNewPasswordConfirm())) {
            log.info("새 비밀번호와 확인값이 일치하지 않음 - 사용자 ID: {}", user.getId());
            throw new ChangePasswordException(ErrorResponse.PASSWORD_CHANGE_PASSWORD_MISMATCH);
        }

        String newPassword = passwordEncoder.encode(request.getNewPassword());
        GdpUser updatedUser = user.changePassword(newPassword);
        userRepository.save(updatedUser);
        log.info("비밀번호 변경 완료 - 사용자 ID: {}", updatedUser.getId());
    }
}
