package com.gotcharoom.gdp.achievements.entity;

import jakarta.persistence.*;
import lombok.*;

import org.hibernate.annotations.UpdateTimestamp;

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

    private String title;

    @Column(columnDefinition = "TEXT")
    private String contentText;

    private String image;

    private String userId;

    // 앨범과 연동한 도전과제 목록
    @OneToMany(mappedBy = "userAlbum", cascade = CascadeType.ALL)
    @Builder.Default // 빈 리스트 작성을 위한 필수 설정
    private List<UserAlbumAchievementList> achievements = new ArrayList<>();

    // 업로드 날짜
    @UpdateTimestamp
    @Column(name = "upload_date")
    private LocalDateTime uploadDate;

    // 연관관계 편의 메서드
    public void addAchievement(UserAlbumAchievementList achievementList) {
        achievements.add(achievementList);
        achievementList.setUserAlbum(this);
    }

}
