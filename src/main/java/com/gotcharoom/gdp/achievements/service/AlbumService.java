package com.gotcharoom.gdp.achievements.service;

import com.gotcharoom.gdp.achievements.entity.UserAlbum;
import com.gotcharoom.gdp.achievements.entity.AlbumAchievementList;
import com.gotcharoom.gdp.achievements.entity.UserSteamAchievement;
import com.gotcharoom.gdp.achievements.model.request.AlbumSaveRequest;
import com.gotcharoom.gdp.achievements.model.response.GetAlbumResponse;
import com.gotcharoom.gdp.achievements.repository.SteamAchievmentRepository;
import com.gotcharoom.gdp.achievements.repository.AlbumAchievementListRepository;
import com.gotcharoom.gdp.achievements.repository.UserAlbumRepository;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AlbumService {
    private final UserAlbumRepository userAlbumRepository;
    private final SteamAchievmentRepository steamAchievmentRepository;
    private final AlbumAchievementListRepository albumAchievementListRepository;

    public AlbumService(UserAlbumRepository userAlbumRepository, SteamAchievmentRepository steamAchievmentRepository, AlbumAchievementListRepository albumAchievementListRepository) {
        this.userAlbumRepository = userAlbumRepository;
        this.steamAchievmentRepository = steamAchievmentRepository;
        this.albumAchievementListRepository = albumAchievementListRepository;
    }

    public GetAlbumResponse getUserAlbum(Long index) {
        UserAlbum album = userAlbumRepository.findById(index)
                .orElseThrow(() -> new EmptyResultDataAccessException("해당 ID의 앨범이 존재하지 않습니다.", 1));

        List<UserSteamAchievement> achievementList = albumAchievementListRepository.findAlbumAchievementList(index);

        return GetAlbumResponse.builder()
                .id(album.getId())
                .title(album.getTitle())
                .contentText(album.getContentText())
                .image(album.getImage())
                .achievements(achievementList)
                .build();
    }

    // 앨범 저장 & 수정 기능
    public int saveUserAlbum(AlbumSaveRequest requestData) {
        // id가 null일시 => 새 앨범 생성
        // id가 null이 아닐 시 => 수정 (중간 테이블(UserAlbumAchievementList도 자동으로 갱신됨)
        UserAlbum newAlbumData = UserAlbum.builder()
                .id(requestData.getId())
                .title(requestData.getTitle())
                .contentText(requestData.getContentText())
                .image(requestData.getImage())
                .userId("test")
                .build();

        requestData.getAchievements().forEach(item -> {
            UserSteamAchievement sample = UserSteamAchievement.builder()
                    .id(item)
                    .build();

            AlbumAchievementList newAchievement = AlbumAchievementList.builder()
                    .achievement(sample)
                    .build();

            newAlbumData.addAchievement(newAchievement);
        });

        try {
            userAlbumRepository.save(newAlbumData);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("잘못된 앨범 번호 또는 도전과제 번호 입니다");
        }


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
            throw new IllegalArgumentException("해당 앨범이 존재하지 않습니다: 22 " + index);

        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new IllegalArgumentException("제약 조건 위반 오류 발생");

        } catch (Exception e) {
            throw new IllegalArgumentException("기타 예외 발생");
        }
    }


    // --------------------------------------- TEST Methods ---------------------------------------

    // 테스트용 request 데이터 제작 메소드
    public AlbumSaveRequest albumRequestDataTest(List<Long> ids) {

        return AlbumSaveRequest.builder()
                .title("타이틀입니다.")
                .contentText("내용물입니다 내용물입니다. 내용물입니다...")
                .image("asdfasdfasdad")
                .achievements(ids)
                .build();
    }
}
