package com.gotcharoom.gdp.achievements.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SteamAchievementItem {
    private String apiname;
    private byte achieved;
    private Long unlocktime;
    private String name;
    private String description;

}
