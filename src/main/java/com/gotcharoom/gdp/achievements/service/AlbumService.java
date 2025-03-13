package com.gotcharoom.gdp.achievements.service;

import com.gotcharoom.gdp.achievements.entity.UserAlbum;
import com.gotcharoom.gdp.achievements.entity.UserAlbumAchievementList;
import com.gotcharoom.gdp.achievements.entity.UserSteamAchievement;
import com.gotcharoom.gdp.achievements.model.request.AlbumSaveRequest;
import com.gotcharoom.gdp.achievements.repository.SteamAchievmentRepository;
import com.gotcharoom.gdp.achievements.repository.UserAlbumRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AlbumService {

    private final SteamAchievmentRepository steamAchievmentRepository;
    private final UserAlbumRepository userAlbumRepository;
    public AlbumService(SteamAchievmentRepository steamAchievmentRepository, UserAlbumRepository userAlbumRepository) {
        this.steamAchievmentRepository = steamAchievmentRepository;
        this.userAlbumRepository = userAlbumRepository;
    }

    public Optional<UserAlbum> getUserAlbum() {
        return userAlbumRepository.findById(1L);
    }

    public int saveUserAlbum(AlbumSaveRequest requestData) {
        UserAlbum newAlbumData = UserAlbum.builder()
                .title(requestData.getTitle())
                .contentText(requestData.getContentText())
                .image(requestData.getImage())
                .userId("test")
                .build();

        requestData.getAchievements().forEach(item -> {
            UserAlbumAchievementList newAchievement = UserAlbumAchievementList.builder()
                    .achievement(item)
                    .build();

            newAlbumData.addAchievement(newAchievement);
        });

        userAlbumRepository.save(newAlbumData);

        return 1;

    }

    @Transactional
    public void deleteUserAlbum(int index) {
        System.out.println("index 값은 : " + index);
        try {
            userAlbumRepository.deleteById((long) index);
        } catch (Exception e) {
            System.out.println("오류가 발생했나? ");
            throw new IllegalArgumentException("해당 앨범을 찾을 수 없습니다.");
        }
    }

    // --------------------------------------- TEST Methods ---------------------------------------

    // 테스트용 request 데이터 제작 메소드
    public AlbumSaveRequest albumRequestDataTest() {
        Optional<UserSteamAchievement> a1 = steamAchievmentRepository.findById(1L);
        Optional<UserSteamAchievement> a2 = steamAchievmentRepository.findById(2L);
        Optional<UserSteamAchievement> a3 = steamAchievmentRepository.findById(3L);

        List<UserSteamAchievement> n1 = new ArrayList<>();

        if(a1.isPresent() && a2.isPresent() && a3.isPresent()) {
            n1.add(a1.get());
            n1.add(a2.get());
            n1.add(a3.get());

            return AlbumSaveRequest.builder()
                    .title("타이틀입니다.")
                    .contentText("내용물입니다 내용물입니다. 내용물입니다...")
                    .image("asdfasdfasdad")
                    .achievements(n1)
                    .build();
        }

        return null;
    }
}
