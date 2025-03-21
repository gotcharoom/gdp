package com.gotcharoom.gdp.global.util;

import com.gotcharoom.gdp.global.enums.YesNo;
import com.gotcharoom.gdp.global.security.model.SocialType;
import com.gotcharoom.gdp.upload.repository.UploadedFileRepository;
import com.gotcharoom.gdp.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UniqueGenerator {

    private final UserRepository userRepository;

    private final UploadedFileRepository uploadedFileRepository;

    public UniqueGenerator(UserRepository userRepository, UploadedFileRepository uploadedFileRepository) {
        this.userRepository = userRepository;
        this.uploadedFileRepository = uploadedFileRepository;
    }


    public String generateUniqueId(SocialType socialType) {
        String id;
        do {
            id = socialType.name() + "-" +  UUID.randomUUID().toString().replace("-", "").substring(0, 12); // 예: google-A1B2C3d4e5f6
        } while (userRepository.findById(id).isPresent()); // 중복이면 다시 생성

        return id;
    }

    public String generateUniqueNickname() {
        String nickname;
        do {
            nickname = "User" +  UUID.randomUUID().toString().replace("-", "").substring(0, 6); // 예: UserA1B2C3
        } while (userRepository.findByNickname(nickname).isPresent()); // 중복이면 다시 생성

        return nickname;
    }

    public String generateUniqueFilename(String fileDir, String fileName, String ext) {
        String uniqueName;
        do {
            // 확장자가 존재하면 .을 추가해서 붙임
            String extWithDot = (ext != null && !ext.isEmpty()) ? "." + ext : "";
            uniqueName = fileName + "-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12) + extWithDot;
        } while (uploadedFileRepository.findByFileDirAndFileNameAndDeletedYn(fileDir, fileName, YesNo.N).isPresent()); // 중복이면 다시 생성

        return uniqueName;
    }
}
