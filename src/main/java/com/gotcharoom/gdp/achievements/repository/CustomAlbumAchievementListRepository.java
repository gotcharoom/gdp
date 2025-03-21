package com.gotcharoom.gdp.achievements.repository;

import com.gotcharoom.gdp.achievements.entity.UserSteamAchievement;

import java.util.List;

public interface CustomAlbumAchievementListRepository {

    List<UserSteamAchievement> findAchievementList(Long albumId);

}
