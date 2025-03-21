package com.gotcharoom.gdp.upload.entity;

import com.gotcharoom.gdp.global.enums.YesNo;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor( access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "uploaded_file")
public class UploadedFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="file_dir", nullable = false)
    private String fileDir;

    @Column(name="file_name", nullable = false, unique = true)
    private String fileName;

    @Column(name="ext")
    private String ext;

    @CreationTimestamp
    @Column(name="created_at", updatable = false)
    private LocalDateTime created_at;

    @Enumerated(EnumType.STRING)
    @Column(name="deleted_yn", nullable = false, length = 1)
    private YesNo deletedYn;

    @Column(name="deleted_at")
    private LocalDateTime deletedAt;

    public UploadedFile updateDelete() {
        LocalDateTime now = LocalDateTime.now();

        return this.toBuilder()
                .deletedAt(now)
                .deletedYn(YesNo.Y)
                .build();
    }
}
