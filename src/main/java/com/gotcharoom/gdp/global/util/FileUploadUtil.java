package com.gotcharoom.gdp.global.util;

import com.gotcharoom.gdp.global.enums.YesNo;
import com.gotcharoom.gdp.upload.entity.UploadedFile;
import com.gotcharoom.gdp.upload.repository.UploadedFileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
@Slf4j
public class FileUploadUtil {

    @Value("${gdp.file-server.url.upload}")
    private String SERVER_UPLOAD_URL;

    @Value("${gdp.file-server.url.download}")
    private String SERVER_DOWNLOAD_URL;

    private final UniqueGenerator uniqueGenerator;
    private final WebClientUtil webClientUtil;
    private final UploadedFileRepository uploadedFileRepository;

    public FileUploadUtil(UniqueGenerator uniqueGenerator, WebClientUtil webClientUtil, UploadedFileRepository uploadedFileRepository) {
        this.uniqueGenerator = uniqueGenerator;
        this.webClientUtil = webClientUtil;
        this.uploadedFileRepository = uploadedFileRepository;
    }

    public String getUploadFullPath(String fileDir, String filename) {
        String path = SERVER_UPLOAD_URL + fileDir + "/" + filename;
        log.info("Generated upload full path: {}", path);
        return path;
    }

    public String getDownloadFullPath(String fileDir, String filename) {
        String path = SERVER_DOWNLOAD_URL + fileDir + "/" + filename;
        log.info("Generated download full path: {}", path);
        return path;
    }

    public String serverUploadFileToFileServer(String fileDir, MultipartFile multipartFile) throws IOException {
        if (multipartFile.isEmpty()) {
            log.warn("No file to upload for directory: {}", fileDir);
            return null;
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String fileNameWithoutExt = extractFilenameWithoutExt(originalFilename);
        String ext = extractExt(originalFilename);
        String serverUploadFileName = uniqueGenerator.generateUniqueFilename(fileDir, fileNameWithoutExt, ext);

        String uploadFullPath = getUploadFullPath(fileDir, serverUploadFileName);

        log.info("Uploading file: original='{}', stored='{}', path='{}'", originalFilename, serverUploadFileName, uploadFullPath);

        ByteArrayResource fileResource = new ByteArrayResource(multipartFile.getBytes()) {
            @Override
            public String getFilename() {
                return serverUploadFileName;
            }
        };

        String response = webClientUtil.put(uploadFullPath, fileResource, String.class, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        log.info("Upload response: {}", response);

        UploadedFile uploadedFile = UploadedFile.builder()
                .fileDir(fileDir)
                .fileName(serverUploadFileName)
                .ext(ext)
                .deletedYn(YesNo.N)
                .build();

        uploadedFileRepository.save(uploadedFile);
        log.info("Saved uploaded file record: dir='{}', name='{}'", fileDir, serverUploadFileName);

        return getDownloadFullPath(fileDir, serverUploadFileName);
    }

    private String extractFilenameWithoutExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        String name = (pos == -1) ? originalFilename : originalFilename.substring(0, pos);
        log.info("Extracted filename without extension: {}", name);
        return name;
    }

    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        String ext = originalFilename.substring(pos + 1);
        log.info("Extracted file extension: {}", ext);
        return ext;
    }

    private String extractOldFileName(String oldImageUrl) {
        int pos = oldImageUrl.lastIndexOf("/");
        String name = oldImageUrl.substring(pos + 1);
        log.info("Extracted old file name from URL: {}", name);
        return name;
    }

    public void deleteOldProfileImage(String fileDir, String oldImageUrl) {
        if (oldImageUrl == null || oldImageUrl.isEmpty()) {
            log.warn("Old image URL is null or empty, skipping deletion.");
            return;
        }

        String oldFileName = extractOldFileName(oldImageUrl);
        String uploadFullPath = getUploadFullPath(fileDir, oldFileName);
        log.info("Attempting to delete old profile image at: {}", uploadFullPath);

        boolean isExist = webClientUtil.existsByHead(uploadFullPath);
        if (!isExist) {
            log.warn("Old file does not exist on server: {}", uploadFullPath);
            return;
        }

        String response = webClientUtil.delete(uploadFullPath, String.class);
        log.info("Delete response: {}", response);

        if (response == null || response.isEmpty()) {
            log.warn("Delete response is null or empty. Aborting DB update.");
            return;
        }

        UploadedFile file = uploadedFileRepository.findByFileDirAndFileNameAndDeletedYn(fileDir, oldFileName, YesNo.N).orElse(null);
        if (file == null) {
            log.warn("No matching file record found in DB for deletion: {}", oldFileName);
            return;
        }

        UploadedFile deletedFile = file.updateDelete();
        uploadedFileRepository.save(deletedFile);
        log.info("Marked file as deleted in DB: {}", oldFileName);
    }
}
