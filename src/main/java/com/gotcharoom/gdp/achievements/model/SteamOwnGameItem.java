package com.gotcharoom.gdp.achievements.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SteamOwnGameItem {
    private int appid;
    private int platime_forever;
    private int playtime_windows_forever;
    private int playtime_mac_forever;
    private int playtime_linux_forever;
    private int playtime_deck_forever;
    private int rtime_last_played;
    private int playtime_disconnected;
}
