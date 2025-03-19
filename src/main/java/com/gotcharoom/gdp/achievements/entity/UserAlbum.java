package com.gotcharoom.gdp.achievements.entity;

import jakarta.persistence.*;
import lombok.*;

import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "user_album")
public class UserAlbum {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(name="content_text", columnDefinition = "TEXT")
    private String contentText;

    private String image;

    @Column(name="user_id", nullable = false)
    private String userId;

    // 앨범과 연동한 도전과제 목록
    @OneToMany(mappedBy = "userAlbum", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default // 빌드 패턴으로 빈 리스트 작성을 위한 필수 설정
    private List<AlbumAchievementList> achievements = new ArrayList<>();

    // 업로드 날짜
    @CreationTimestamp
    @Column(name = "upload_date")
    private LocalDateTime uploadDate;

    // 연관관계 편의 메서드
    public void addAchievement(AlbumAchievementList achievementList) {
        achievements.add(achievementList);
        achievementList.updateAlbum(this);
    }

}
