package com.gotcharoom.gdp.upload.service;

import com.gotcharoom.gdp.global.util.FileUploadUtil;
import com.gotcharoom.gdp.user.entity.GdpUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class UploadFileService {

    @Value("${gdp.file-server.dir.profile}")
    private String PROFILE_DIR;

    private final FileUploadUtil fileUploadUtil;

    public UploadFileService(FileUploadUtil fileUploadUtil) {
        this.fileUploadUtil = fileUploadUtil;
    }

    public String uploadProfileFile(String oldImageUrl, MultipartFile imageFile) throws IOException {
        fileUploadUtil.deleteOldImage(PROFILE_DIR, oldImageUrl);

        return fileUploadUtil.serverUploadFile(PROFILE_DIR, imageFile);
    }
}
