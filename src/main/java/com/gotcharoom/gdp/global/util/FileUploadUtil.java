package com.gotcharoom.gdp.global.util;

import com.gotcharoom.gdp.global.security.service.UniqueGenerator;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Component
public class FileUploadUtil {

    private final UniqueGenerator uniqueGenerator;

    public FileUploadUtil(UniqueGenerator uniqueGenerator) {
        this.uniqueGenerator = uniqueGenerator;
    }

    /**
     * 파일이 저장되는 전체 경로를 반환
     *
     * @param filename 서버에 업로드되는 파일명
     * @param fileDir  파일이 저장되는 경로
     * @return 전체 파일 경로
     */
    public String getFullPath(String filename, String fileDir) {
        return fileDir + filename;
    }

    /**
     * 서버에 파일을 업로드하는 메서드
     *
     * @param multipartFile 업로드할 파일
     * @param fileDir       저장할 경로
     * @return 저장된 파일명 (UUID 포함)
     * @throws IOException 파일 저장 중 오류 발생 시
     */
    public String serverUploadFile(MultipartFile multipartFile, String fileDir) throws IOException {
        if (multipartFile.isEmpty()) { // 파일이 없으면 null 반환
            return null;
        }

        String originalFilename = multipartFile.getOriginalFilename(); // 원래 파일명
        String serverUploadFileName = uniqueGenerator.generateUniqueFilename(fileDir, originalFilename); // UUID 기반 파일명 생성

        // 저장: (서버에 업로드되는 파일명, 업로드되는 경로)
        multipartFile.transferTo(new File(getFullPath(serverUploadFileName, fileDir)));

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

    public void deleteOldImage(String oldImageUrl) {
        File oldFile = new File(oldImageUrl);

        if (!oldFile.exists()) {
            return ;
        }

        if (!oldFile.isFile()) {
            return;
        }

        boolean isDeleted = oldFile.delete();  // 기존 이미지 삭제

        if(!isDeleted) {
            throw new RuntimeException("Failed to delete image: " + oldFile.getAbsolutePath());
        }
    }
}
