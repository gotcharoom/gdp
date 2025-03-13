package com.gotcharoom.gdp.achievements.service;

import com.gotcharoom.gdp.achievements.entity.UserAlbum;
import com.gotcharoom.gdp.achievements.entity.UserAlbumAchievementList;
import com.gotcharoom.gdp.achievements.entity.UserSteamAchievement;
import com.gotcharoom.gdp.achievements.model.request.AlbumSaveRequest;
import com.gotcharoom.gdp.achievements.repository.SteamAchievmentRepository;
import com.gotcharoom.gdp.achievements.repository.UserAlbumRepository;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
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

    public UserAlbum getUserAlbum() {
        return userAlbumRepository.findById(1L)
                .orElseThrow(() -> new EmptyResultDataAccessException("해당 ID의 앨범이 존재하지 않습니다.", 1));
    }

    // 앨범 저장 기능
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

    // 앨범 삭제 기능
    @Transactional
    public void deleteUserAlbum(Long index) {
        System.out.println("index 값은 : " + index);
        try {
            // deleteById 메소드를 쓰면 Exception이 발생 안함 -> 오류 캐치를 위해 findById와 delete 메소드 사용
            UserAlbum album = userAlbumRepository.findById(index)
                    .orElseThrow(() -> new EmptyResultDataAccessException(1));
            userAlbumRepository.delete(album);

        } catch (EmptyResultDataAccessException e) {
            System.out.println("오류가 발생했나? " + e);
            throw new EmptyResultDataAccessException("해당 앨범이 존재하지 않습니다: 22 " + index, 1);

        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new DataIntegrityViolationException("제약 조건 위반 오류 발생");

        } catch (Exception e) {
            throw new IllegalArgumentException("기타 예외 발생");
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
