package com.gotcharoom.gdp.achievements.service;

import com.gotcharoom.gdp.achievements.entity.UserSteamAchievement;
import com.gotcharoom.gdp.achievements.model.SteamAchievementItem;
import com.gotcharoom.gdp.achievements.model.steamAPI.SteamOwnGameItem;
import com.gotcharoom.gdp.achievements.model.steamAPI.SteamOwnGames;
import com.gotcharoom.gdp.achievements.model.steamAPI.SteamPlayerStat;
import com.gotcharoom.gdp.achievements.repository.SteamAchievmentRepository;
import com.gotcharoom.gdp.global.util.WebClientUtil;
import com.gotcharoom.gdp.platform.repository.UserPlatformRepository;
import com.gotcharoom.gdp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class LinkageService {

    private final SteamAchievmentRepository steamAchievmentRepository;
    private final UserRepository userRepository;
    private final UserPlatformRepository userPlatformRepository;

    private static final String STEAM_API_KEY = "C9926F5CBA82A0101A90A59BE0665295";  // 발급받은 API 키
    private static final String STEAM_API_URL = "https://api.steampowered.com";

    private final WebClientUtil webClientUtil;
    
    // 연동한 스팀 계정의 게임 도전과제 목록 전부 불러오기
    public List<SteamPlayerStat> getSteamPlayerAchievement(String userName) {
        // 1. 현재 접속한 유저의 스팀id 확인
        // String steamId = "76561198230645968";
        String steamId = userPlatformRepository.findUserSteamId(userName).orElseThrow();

        // 2. 소유한 게임 목록 불러오기
        List<Integer>appids = getSteamOwnedGamesAppid(steamId);

        List<SteamPlayerStat> result = new ArrayList<>();
        
        // 3. 소유한 게임들의 도전과제 불러오기
        for(int i : appids) {
            // 4. 도전과제 미보유 게임에 대한 요청은 400 BadRequest가 발생하거나 도전 과제 데이터가 넘어오지 않음 -> 예외 처리 후 다음 게임 정보 요청
            try {
                result.add(getSteamPlayerAchievementsOne(i, steamId));
            } catch(WebClientResponseException.BadRequest e) {
                System.out.println("400 오류 발생 번호는" + i);
            } catch(IllegalArgumentException e) {
            }
        }

        return result;
    }

    // 특정 스팀 게임 도전과제 불러오기
    public SteamPlayerStat getSteamPlayerAchievementsOne(int appId, String steamId) throws IllegalArgumentException {
        String target = STEAM_API_URL + "/ISteamUserStats/GetPlayerAchievements/v0001/";

        String url = UriComponentsBuilder.fromUriString(target)
                .queryParam("key", STEAM_API_KEY)
                .queryParam("appid", appId)
                .queryParam("steamid", steamId)
                .queryParam("l", "koreana")
                .toUriString()
                .trim();

        SteamPlayerStat result = webClientUtil.get(url, SteamPlayerStat.class, "playerstats");

        // 유효성 검사 1. 도전과제가 있는 게임인지 확인
        if(result.getAchievements() == null) {
            System.out.println("유효성 오류 검사 1 발생 번호는 " + appId);
            throw new IllegalArgumentException("도전과제가 없는 게임입니다. appid = " + appId);
        }
        
        // 달성한 도전과제만 추출
        List<SteamAchievementItem> filteredList = result.getAchievements().stream()
                .filter(item -> item.getAchieved() == 1)
                .collect(Collectors.toList());
        
        // 유효성 검사 2. 달성한 도전과제가 하나도 없는지 확인
        if (filteredList.isEmpty()) {
            System.out.println("유효성 오류 검사 2 발생 번호는 " + appId);
            throw new IllegalArgumentException(" 달성한 도전과제가 하나도 없는 게임입니다. appid = " + appId);
        }

        // 추출된 목록을 새 객체에 담아 return
        return result.toBuilder(filteredList);

    }

    // 보유한 스팀 게임 appid 목록 불러오기
    private List<Integer> getSteamOwnedGamesAppid(String steamId) {
        String target = STEAM_API_URL + "/IPlayerService/GetOwnedGames/v0001/";

        // 1. 사용자 게임 목록 요청
        String url = UriComponentsBuilder.fromUriString(target)
                .queryParam("key", STEAM_API_KEY)
                .queryParam("steamid", steamId)
                .queryParam("format", "json")
                .toUriString()
                .trim();

        SteamOwnGames result =  webClientUtil.get(url, SteamOwnGames.class, "response");

        // 2. appid만 추출
        List<SteamOwnGameItem> games = result.getGames();
        List<Integer> appids = new ArrayList<>();
        for(SteamOwnGameItem i : games) {
            appids.add(i.getAppid());
        }
        return appids;
    }

    // 연동한 스팀 도전과제를 DB에 저장
    public int saveSteamAchievements(List<SteamPlayerStat> achievementList) {
        int count = 0;

        // 저장을 위해 포맷
        for(SteamPlayerStat i : achievementList) {
            for(SteamAchievementItem j : i.getAchievements()) {
                try {
                    steamAchievmentRepository.save(UserSteamAchievement.builder()
                            .steamID(i.getSteamID())
                            .gameName(i.getGameName())
                            .apiname(j.getApiname())
                            .unlocktime(j.getUnlocktime())
                            .name(j.getName())
                            .description(j.getDescription())
                            .build());

                    // 저장에 성공한 항목 수 집계
                    count++;

                } catch (DataIntegrityViolationException e) { // 이미 연동한 도전과제인지 유효성 검사
                    System.out.println("이미 존재하는 데이터입니다." + i);
                }
            }
        }

        return count;
    }

    // --------------------------------------- TEST Methods ---------------------------------------


    // 외부 api 사용 테스트 (GetSchemaForGame)
    public Object test() {
        String target = STEAM_API_URL + "/ISteamUserStats/GetSchemaForGame/v2/";

        String url = UriComponentsBuilder.fromUriString(target)
                .queryParam("key", STEAM_API_KEY)
                .queryParam("appid", "429660")
                .queryParam("l", "koreana")
                .toUriString()
                .trim();

        return webClientUtil.get(url, Object.class);

    }

    // 외부 api 사용 테스트 (GetOwnedGames)
    public Object test2() {
        String target = STEAM_API_URL + "/IPlayerService/GetOwnedGames/v0001/";

        String url = UriComponentsBuilder.fromUriString(target)
                .queryParam("key", STEAM_API_KEY)
                .queryParam("steamid", "76561198230645968")
                .queryParam("format", "json")
                .toUriString()
                .trim();

        return webClientUtil.get(url, Object.class, "response");

    }


}
