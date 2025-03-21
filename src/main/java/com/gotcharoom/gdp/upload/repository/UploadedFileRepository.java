package com.gotcharoom.gdp.upload.repository;

import com.gotcharoom.gdp.global.enums.YesNo;
import com.gotcharoom.gdp.upload.entity.UploadedFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UploadedFileRepository extends JpaRepository<UploadedFile, Long> {

    Optional<UploadedFile> findByFileDirAndFileNameAndDeletedYn(String fileDir, String fileName, YesNo deleteYn);
}
