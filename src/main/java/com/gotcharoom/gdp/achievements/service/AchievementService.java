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
import org.springframework.web.reactive.function.client.WebClientResponseException;
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

    private final WebClientUtil webClientUtil;

    public AchievementService(WebClient.Builder webClientBuilder, SteamAchievmentRepository steamAchievmentRepository, UserRepository userRepository, WebClientUtil webClientUtil) {
        this.webClient = webClientBuilder.baseUrl(STEAM_API_URL).build();
        this.steamAchievmentRepository = steamAchievmentRepository;
        this.userRepository = userRepository;
        this.webClientUtil = webClientUtil;
    }

    // 해당 스팀 게임 도전과제 목록 전부 불러오기
    public List<SteamPlayerStat> getSteamPlayerAchievement(String userName) {

        // 1. 현재 접속한 유저의 스팀id 확인 (현재는 생략)
        String steamId = "76561198230645968";
        // 2. 소유한 게임 목록 불러오기
        List<Integer>appids = getSteamOwnedGamesAppid(steamId);

        List<SteamPlayerStat> result = new ArrayList<>();

        // 3. 소유한 게임들의 도전과제 불러오기
        for(int i : appids) {
            try {
                result.add(getSteamPlayerAchievementsOne(i, steamId).getPlayerstats());
            } catch(WebClientResponseException.BadRequest e) {
                System.out.println("오류 발생 번호는" + i);
                continue;
            }
        }

        return result;
    }

    // 특정 스팀 게임 도전과제 불러오기
    private SteamAchievementRequest getSteamPlayerAchievementsOne(int appId, String steamId) {
        String target = "https://api.steampowered.com/ISteamUserStats/GetPlayerAchievements/v0001/";

        String url = UriComponentsBuilder.fromUriString(target)
                .queryParam("key", STEAM_API_KEY)
                .queryParam("appid", appId)
                .queryParam("steamid", steamId)
                .queryParam("l", "koreana")
                .toUriString()
                .trim();

        return webClientUtil.get(url, SteamAchievementRequest.class);

    }

    // 보유한 스팀 게임 appid 목록 불러오기
    private List<Integer> getSteamOwnedGamesAppid(String steamId) {
        String target = "http://api.steampowered.com/IPlayerService/GetOwnedGames/v0001/";

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

}
