package com.gotcharoom.gdp.achievements.model.steamAPI;

import com.gotcharoom.gdp.achievements.model.SteamAchievementItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SteamPlayerStat {
    private String steamID;
    private String gameName;
    private List<SteamAchievementItem> achievements;
    private boolean success;

    // 새 객체 생성 (builder 패턴 방식)
    public SteamPlayerStat toBuilder(List<SteamAchievementItem> newAchievements) {
        return SteamPlayerStat.builder()
                .steamID(steamID)
                .gameName(gameName)
                .achievements(newAchievements)
                .success(success)
                .build();
    }
}
