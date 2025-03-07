package com.gotcharoom.gdp.achievements.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlayerStat {
    private String steamID;
    private String gameName;
    private List<SteamAchievementItem> achievements;
    private boolean success;

}
