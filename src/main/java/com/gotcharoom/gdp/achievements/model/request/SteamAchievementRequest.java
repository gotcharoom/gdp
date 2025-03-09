package com.gotcharoom.gdp.achievements.model.request;

import com.gotcharoom.gdp.achievements.model.SteamPlayerStat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SteamAchievementRequest {
    private SteamPlayerStat playerstats;
}
