package com.gotcharoom.gdp.achievements.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(
        name = "user_album_achievment_list",
        uniqueConstraints = {
            @UniqueConstraint(name = "unique_user_steam_achievement", columnNames = {
                    "USER_ALBUM_ID",
                    "USER_ACHIEVEMENT_ID",
                })
        }
)
// 앨범에 저장된 도전과제 목록 (중간 매핑 테이블)
public class AlbumAchievementList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ALBUM_ID")
    private UserAlbum userAlbum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ACHIEVEMENT_ID")
    private UserSteamAchievement achievement;

    // album 연관 관계용 setter 메소드
    public void updateAlbum(UserAlbum newAlbum) {
        this.userAlbum = newAlbum;
    }

}
