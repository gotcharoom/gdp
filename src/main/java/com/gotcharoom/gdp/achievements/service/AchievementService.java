package com.gotcharoom.gdp.achievements.service;

import com.gotcharoom.gdp.achievements.model.SteamOwnGameItem;
import com.gotcharoom.gdp.achievements.model.SteamPlayerStat;
import com.gotcharoom.gdp.achievements.model.request.SteamAchievementRequest;
import com.gotcharoom.gdp.achievements.model.request.SteamOwnGamesRequest;
import com.gotcharoom.gdp.achievements.repository.SteamAchievmentRepository;
import com.gotcharoom.gdp.global.util.WebClientUtil;
import com.gotcharoom.gdp.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

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

    // 해당 스팀 게임 도전과제 불러오기
    public SteamAchievementRequest getSteamPlayerAchievementsOne(int appId) {
        String target = "https://api.steampowered.com/ISteamUserStats/GetPlayerAchievements/v0001/";
        // 사용자 steamId
        String steamId = "76561198230645968";

        String url = UriComponentsBuilder.fromUriString(target)
                .queryParam("key", STEAM_API_KEY)
                .queryParam("appid", appId)
                .queryParam("steamid", steamId)
                .queryParam("l", "koreana")
                .toUriString()
                .trim();

        return webClientUtil.get(url, SteamAchievementRequest.class);

    }

    // 해당 스팀 게임 도전과제 목록 전부 불러오기
    public List<SteamPlayerStat> getSteamPlayerAchievement() {
        List<Integer>appids = getSteamOwnedGamesAppid();

        System.out.println(appids);

        List<SteamPlayerStat> result = new ArrayList<>();

        for(int i : appids) {
            System.out.println("지금 번호는" + i);
            result.add(getSteamPlayerAchievementsOne(i).getPlayerstats());
            System.out.println();
        }

        return result;
    }

    // 보유한 스팀 게임 appid 목록 불러오기
    public List<Integer> getSteamOwnedGamesAppid() {
        String target = "http://api.steampowered.com/IPlayerService/GetOwnedGames/v0001/";
        // 사용자 steamId
        String steamId = "76561198230645968";

        // 1. 사용자 게임 목록 요청
        String url = UriComponentsBuilder.fromUriString(target)
                .queryParam("key", STEAM_API_KEY)
                .queryParam("steamid", steamId)
                .queryParam("format", "json")
                .toUriString()
                .trim();

        SteamOwnGamesRequest result =  webClientUtil.get(url, SteamOwnGamesRequest.class);

        // 2. appid만 추출
        List<SteamOwnGameItem> games = result.getResponse().getGames();
        List<Integer> appids = new ArrayList<>();
        for(SteamOwnGameItem i : games) {
            appids.add(i.getAppid());
        }
        return appids;
    }

    // 해당 게임이 도전과제가 있는지 확인
    public String hasAchievements(int appId) {
        String target = "https://api.steampowered.com/ISteamUserStats/GetSchemaForGame/v2/";

        String url = UriComponentsBuilder.fromUriString(target)
                .queryParam("key", STEAM_API_KEY)
                .queryParam("steamid", "76561198230645968")
                .queryParam("appid", appId)
                .toUriString()
                .trim();

        return webClientUtil.get(url, String.class);
    }

}
