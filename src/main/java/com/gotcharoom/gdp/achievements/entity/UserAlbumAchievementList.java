package com.gotcharoom.gdp.achievements.entity;

import com.gotcharoom.gdp.achievements.model.SteamAchievementItem;
import com.gotcharoom.gdp.achievements.model.SteamPlayerStat;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "user_album_achievment_list")
public class UserAlbumAchievementList {
    // 앨범에 저장된 도전과제 목록 (중간 매핑 테이블)

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ALBUM_ID")
    private UserAlbum userAlbum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ACHIEVEMENT_ID")
    private UserSteamAchievement achievement;

    // 새 객체 생성 (builder 패턴 방식)
    public UserAlbumAchievementList toBuilder(UserAlbum newAlbum) {
        return UserAlbumAchievementList.builder()
                .userAlbum(newAlbum)
                .achievement(achievement)
                .build();
    }

}
