package com.gotcharoom.gdp.global.util;

import com.gotcharoom.gdp.global.enums.YesNo;
import com.gotcharoom.gdp.global.security.service.UniqueGenerator;
import com.gotcharoom.gdp.upload.entity.UploadedFile;
import com.gotcharoom.gdp.upload.repository.UploadedFileRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;

import java.io.File;
import java.io.IOException;

@Component
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

    /**
     * 파일이 저장되는 전체 경로를 반환
     *
     * @param fileDir  파일이 저장되는 경로
     * @param filename 서버에 업로드되는 파일명
     * @return 전체 파일 경로
     */
    public String getUploadFullPath(String fileDir, String filename) {
        return SERVER_UPLOAD_URL + fileDir + "/" + filename;
    }

    public String getDownloadFullPath(String fileDir, String filename) {
        return SERVER_DOWNLOAD_URL + fileDir + "/" + filename;
    }

    public String serverUploadFileToFileServer(String fileDir, MultipartFile multipartFile) throws IOException {
        if (multipartFile.isEmpty()) { // 파일이 없으면 null 반환
            return null;
        }

        String originalFilename = multipartFile.getOriginalFilename(); // 원래 파일명
        String fileNameWithoutExt = extractFilenameWithoutExt(originalFilename);
        String ext = extractExt(originalFilename);
        String serverUploadFileName = uniqueGenerator.generateUniqueFilename(fileDir, fileNameWithoutExt, ext); // UUID 기반 파일명 생성

        // 업로드 경로 생성
        String uploadFullPath = getUploadFullPath(fileDir, serverUploadFileName);

        ByteArrayResource fileResource = new ByteArrayResource(multipartFile.getBytes()) {
            @Override
            public String getFilename() {
                return serverUploadFileName; // 서버에 저장할 파일명 반환
            }
        };

        // WebClient PUT 요청 실행
        String response = webClientUtil.put(uploadFullPath, BodyInserters.fromResource(fileResource), String.class, MediaType.APPLICATION_OCTET_STREAM_VALUE);

        UploadedFile uploadedFile = UploadedFile.builder()
                .fileDir(fileDir)
                .fileName(serverUploadFileName)
                .ext(ext)
                .deletedYn(YesNo.N)
                .build();

        uploadedFileRepository.save(uploadedFile);

        return getDownloadFullPath(fileDir, serverUploadFileName);
    }

    private String extractFilenameWithoutExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        if (pos == -1) {
            return originalFilename; // 확장자가 없는 경우 전체 파일명을 반환
        }
        return originalFilename.substring(0, pos); // 확장자 앞부분만 반환
    }

    /**
     * 원래 파일명에서 확장자 추출 (.jpg, .png 등)
     *
     * @param originalFilename 원본 파일명
     * @return 확장자
     */
    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

    private String extractOldFileName(String oldImageUrl) {
        int pos = oldImageUrl.lastIndexOf("/");
        return oldImageUrl.substring(pos + 1);
    }

    public void deleteOldProfileImage(String fileDir, String oldImageUrl) {
        if (oldImageUrl == null || oldImageUrl.isEmpty()) {
            return ;
        }

        // 파일 확인
        String oldFileName = extractOldFileName(oldImageUrl);
        String uploadFullPath = getUploadFullPath(fileDir, oldFileName);

        File oldFile = new File(uploadFullPath);

        if (!oldFile.exists()) {
            return ;
        }

        if (!oldFile.isFile()) {
            return;
        }

        // 삭제 실행
        String response = webClientUtil.delete(uploadFullPath, String.class);

        if (response == null || response.isEmpty()) {
            return;
        }

        UploadedFile file = uploadedFileRepository.findByFileDirAndFileNameAndDeletedYn(fileDir, oldFileName, YesNo.N).orElse(null);

        if(file == null) {
            return;
        }

        UploadedFile deletedFile = file.updateDelete();
        uploadedFileRepository.save(deletedFile);
    }
}
