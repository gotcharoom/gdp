package com.gotcharoom.gdp.achievements.service;

import com.gotcharoom.gdp.achievements.model.response.SteamAchievementResponse;
import com.gotcharoom.gdp.achievements.repository.SteamAchievmentRepository;
import com.gotcharoom.gdp.global.util.WebClientUtil;
import com.gotcharoom.gdp.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class AchievementService {

    private final WebClient webClient;
    private final SteamAchievmentRepository steamAchievmentRepository;
    private final UserRepository userRepository;

    private static final String STEAM_API_KEY = "C9926F5CBA82A0101A90A59BE0665295";  // 발급받은 API 키
    private static final String STEAM_API_URL = "https://api.steampowered.com";

//    private static final String appId = "429660";
//    private static final String steamId = "76561198230645968";

    private final WebClientUtil webClientUtil;

    public AchievementService(WebClient.Builder webClientBuilder, SteamAchievmentRepository steamAchievmentRepository, UserRepository userRepository, WebClientUtil webClientUtil) {
        this.webClient = webClientBuilder.baseUrl(STEAM_API_URL).build();
        this.steamAchievmentRepository = steamAchievmentRepository;
        this.userRepository = userRepository;
        this.webClientUtil = webClientUtil;
    }

    public SteamAchievementResponse getSteamAchievement() {
        // 사용자 steamId

        String steamId = "76561198230645968";
        // 게임 id
        String appId = "429660";

        String target = "https://api.steampowered.com/ISteamUserStats/GetPlayerAchievements/v0001/";


        String url = UriComponentsBuilder.fromUriString(target)
                .queryParam("key", STEAM_API_KEY)
                .queryParam("appid", appId)
                .queryParam("steamid", steamId)
                .queryParam("l", "koreana")
                .toUriString()
                .trim();

        return webClientUtil.get(url, SteamAchievementResponse.class);

    }

}
