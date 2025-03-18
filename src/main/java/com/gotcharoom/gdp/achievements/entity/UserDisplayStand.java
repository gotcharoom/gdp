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
@Table(name = "user_display_stand")
public class UserDisplayStand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String contentText;

    private String image;

    @Column(nullable = false)
    private String userId;

    // 전시대와 연동할 앨범 목록
    @OneToMany(mappedBy = "userDisplayStand", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default // 빈 리스트 작성을 위한 필수 설정
    private List<DisplayStandAlbumList> albums = new ArrayList<>();

    // 업로드 날짜
    @CreationTimestamp
    @Column(name = "upload_date")
    private LocalDateTime uploadDate;

    // 연관관계 편의 메서드
    public void addAlbum(DisplayStandAlbumList albumList) {
        albums.add(albumList);
        albumList.updateDisplayStand(this);
    }
}
