package com.gotcharoom.gdp.achievements.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "user_album_achievment_list")
public class UserAlbumAchievementList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ALBUM_ID")
    private UserAlbum userAlbum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ACHIEVEMENT_ID")
    private UserSteamAchievement achievement;


}
