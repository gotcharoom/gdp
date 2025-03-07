package com.gotcharoom.gdp.achievements.repository;

import com.gotcharoom.gdp.achievements.entity.SteamAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SteamAchievmentRepository extends JpaRepository<SteamAchievement, String>  {
}
