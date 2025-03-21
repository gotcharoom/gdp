package com.gotcharoom.gdp.upload.service;

import com.gotcharoom.gdp.global.util.FileUploadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
public class UploadFileService {

    @Value("${gdp.file-server.dir.profile}")
    private String PROFILE_DIR;

    private final FileUploadUtil fileUploadUtil;

    public UploadFileService(FileUploadUtil fileUploadUtil) {
        this.fileUploadUtil = fileUploadUtil;
    }

    public String uploadProfileFile(String oldImageUrl, MultipartFile imageFile) throws IOException {
        log.info("프로필 이미지 업로드 요청 수신 - 기존 이미지 URL: {}", oldImageUrl);

        fileUploadUtil.deleteOldProfileImage(PROFILE_DIR, oldImageUrl);
        log.info("기존 프로필 이미지 삭제 처리 완료");

        String uploadedUrl = fileUploadUtil.serverUploadFileToFileServer(PROFILE_DIR, imageFile);
        log.info("새 프로필 이미지 업로드 완료 - 저장 경로: {}", uploadedUrl);

        return uploadedUrl;
    }
}
