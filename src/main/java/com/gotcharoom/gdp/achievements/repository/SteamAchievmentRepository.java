package com.gotcharoom.gdp.achievements.repository;

import com.gotcharoom.gdp.achievements.entity.UserSteamAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SteamAchievmentRepository extends JpaRepository<UserSteamAchievement, Long>  {

}
