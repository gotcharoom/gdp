package com.gotcharoom.gdp.global.util;

import com.gotcharoom.gdp.global.enums.YesNo;
import com.gotcharoom.gdp.global.security.service.UniqueGenerator;
import com.gotcharoom.gdp.upload.entity.UploadedFile;
import com.gotcharoom.gdp.upload.repository.UploadedFileRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Component
public class FileUploadUtil {

    @Value("${gdp.file-server.url}")
    private String SERVER_URL;

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
    public String getFullPath(String fileDir, String filename) {
        return SERVER_URL + fileDir + "/" + filename;
    }

    /**
     * 서버에 파일을 업로드하는 메서드
     *
     * @param fileDir       저장할 경로
     * @param multipartFile 업로드할 파일
     * @return 저장된 파일명 (UUID 포함)
     * @throws IOException 파일 저장 중 오류 발생 시
     */
    public String serverUploadFile(String fileDir, MultipartFile multipartFile) throws IOException {
        if (multipartFile.isEmpty()) { // 파일이 없으면 null 반환
            return null;
        }

        String originalFilename = multipartFile.getOriginalFilename(); // 원래 파일명
        String serverUploadFileName = uniqueGenerator.generateUniqueFilename(fileDir, originalFilename); // UUID 기반 파일명 생성
        String ext = extractExt(originalFilename);

        // 저장: (서버에 업로드되는 파일명, 업로드되는 경로)
        multipartFile.transferTo(new File(getFullPath(fileDir, serverUploadFileName)));

        UploadedFile uploadedFile = UploadedFile.builder()
                .fileDir(fileDir)
                .fileName(serverUploadFileName)
                .ext(ext)
                .deletedYn(YesNo.N)
                .build();

        uploadedFileRepository.save(uploadedFile);

        return serverUploadFileName;
    }

    public String serverUploadFileToFileServer(String fileDir, MultipartFile multipartFile) throws IOException {
        if (multipartFile.isEmpty()) { // 파일이 없으면 null 반환
            return null;
        }

        String originalFilename = multipartFile.getOriginalFilename(); // 원래 파일명
        String serverUploadFileName = uniqueGenerator.generateUniqueFilename(fileDir, originalFilename); // UUID 기반 파일명 생성
        String ext = extractExt(originalFilename);

        // 저장: (서버에 업로드되는 파일명, 업로드되는 경로)
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", multipartFile.getResource()); // MultipartFile을 Resource로 변환하여 전송
        body.add("filename", serverUploadFileName);

        String serverDir = SERVER_URL + fileDir;
        String response = webClientUtil.post(serverDir, body, String.class);

        UploadedFile uploadedFile = UploadedFile.builder()
                .fileDir(fileDir)
                .fileName(serverUploadFileName)
                .ext(ext)
                .deletedYn(YesNo.N)
                .build();

        uploadedFileRepository.save(uploadedFile);

        return serverUploadFileName;
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

    public void deleteOldImage(String fileDir, String oldImageUrl) {
        if (oldImageUrl == null || oldImageUrl.isEmpty()) {
            return ;
        }

        File oldFile = new File(oldImageUrl);

        if (!oldFile.exists()) {
            return ;
        }

        if (!oldFile.isFile()) {
            return;
        }

        boolean isDeleted = oldFile.delete();  // 기존 이미지 삭제

        if(!isDeleted) {
            //            throw new RuntimeException("Failed to delete image: " + oldFile.getAbsolutePath());
            return;
        }

        String oldFileName = extractOldFileName(oldImageUrl);
        UploadedFile file = uploadedFileRepository.findByFileDirAndFileNameAndDeletedYn(fileDir, oldFileName, YesNo.N).orElse(null);

        if(file == null) {
            return;
        }

        UploadedFile deletedFile = file.updateDelete();
        uploadedFileRepository.save(deletedFile);
    }
}
