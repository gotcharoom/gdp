package com.gotcharoom.gdp.achievements.repository;

import com.gotcharoom.gdp.achievements.entity.UserSteamAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SteamAchievmentRepository extends JpaRepository<UserSteamAchievement, Long>  {

//    @Query("SELECT * FROM user_steam_achievement o JOIN FETCH o.orderProducts op JOIN FETCH op.product WHERE o.id = :id")
//    List<UserSteamAchievement> findAlbumAchievements(@Param("id") Long id);
}
