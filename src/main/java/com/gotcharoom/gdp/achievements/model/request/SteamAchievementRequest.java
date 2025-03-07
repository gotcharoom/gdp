package com.gotcharoom.gdp.achievements.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SteamAchievementRequest {
    private String steamId;
    private String apiId;
    private String steamApiKey;

}
