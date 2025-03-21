package com.gotcharoom.gdp.achievements.repository;

import com.gotcharoom.gdp.achievements.entity.QAlbumAchievementList;
import com.gotcharoom.gdp.achievements.entity.QUserAlbum;
import com.gotcharoom.gdp.achievements.entity.QUserSteamAchievement;
import com.gotcharoom.gdp.achievements.entity.UserSteamAchievement;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class CustomAlbumAchievementListRepositoryImpl implements CustomAlbumAchievementListRepository {
    private final JPAQueryFactory jpaQueryFactory;

    private final QAlbumAchievementList albumAchievementList = QAlbumAchievementList.albumAchievementList;
    private final QUserAlbum userAlbum = QUserAlbum.userAlbum;
    private final QUserSteamAchievement userSteamAchievement = QUserSteamAchievement.userSteamAchievement;

    @Override
    public List<UserSteamAchievement> findAchievementList(Long albumId) {

        return jpaQueryFactory
                .select(userSteamAchievement)
                .from(albumAchievementList)
                .join(albumAchievementList.achievement, userSteamAchievement)
                .join(albumAchievementList.userAlbum, userAlbum)
                .where(albumAchievementList.userAlbum.id.eq(albumId))
                .fetch();

    }
}
