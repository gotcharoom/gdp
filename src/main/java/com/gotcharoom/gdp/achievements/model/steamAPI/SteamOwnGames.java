package com.gotcharoom.gdp.achievements.model.steamAPI;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SteamOwnGames {
    private int game_count;
    private List<SteamOwnGameItem> games;
}