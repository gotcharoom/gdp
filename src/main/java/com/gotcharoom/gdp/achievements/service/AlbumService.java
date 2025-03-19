package com.gotcharoom.gdp.achievements.service;

import com.gotcharoom.gdp.achievements.entity.UserAlbum;
import com.gotcharoom.gdp.achievements.entity.AlbumAchievementList;
import com.gotcharoom.gdp.achievements.entity.UserSteamAchievement;
import com.gotcharoom.gdp.achievements.model.request.AlbumSaveRequest;
import com.gotcharoom.gdp.achievements.model.response.AlbumGetListResponse;
import com.gotcharoom.gdp.achievements.model.response.AlbumGetResponse;
import com.gotcharoom.gdp.achievements.repository.AlbumAchievementListRepository;
import com.gotcharoom.gdp.achievements.repository.UserAlbumRepository;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AlbumService {
    private final UserAlbumRepository userAlbumRepository;
    private final AlbumAchievementListRepository albumAchievementListRepository;

    public AlbumService(UserAlbumRepository userAlbumRepository, AlbumAchievementListRepository albumAchievementListRepository) {
        this.userAlbumRepository = userAlbumRepository;
        this.albumAchievementListRepository = albumAchievementListRepository;
    }

    // 앨범 전체 목록 가져오기
    public Page<AlbumGetListResponse> getUserAlbums(int pageNo, int pageSize) {

        return userAlbumRepository.findPageBy(PageRequest.of(pageNo, pageSize, Sort.by("id").descending()));

    }

    // 앨범 내용 가져오기 (details)
    public AlbumGetResponse getUserAlbumOne(Long index) {
        UserAlbum album = userAlbumRepository.findById(index)
                .orElseThrow(() -> new EmptyResultDataAccessException("해당 ID의 앨범이 존재하지 않습니다.", 1));

        List<UserSteamAchievement> achievementList = albumAchievementListRepository.findAchievementList(index);

        return AlbumGetResponse.builder()
                .id(album.getId())
                .title(album.getTitle())
                .contentText(album.getContentText())
                .image(album.getImage())
                .achievements(achievementList)
                .build();
    }

    // 앨범 저장 기능
    public void saveUserAlbum(AlbumSaveRequest requestData) {
        // 중간 테이블(AlbumAchievementList)도 자동으로 갱신됨

        UserAlbum newAlbumData = UserAlbum.builder()
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
    }

    public void editUserAlbum(AlbumSaveRequest requestData) {
        // 중간 테이블(AlbumAchievementList)도 자동으로 갱신됨

        // 생성 날짜 불변을 위해 기존 데이터 조회
        UserAlbum oldAlbumData = userAlbumRepository.findById(requestData.getId())
                .orElseThrow(() -> new EmptyResultDataAccessException("해당 ID의 앨범이 존재하지 않습니다.", 1));

        UserAlbum newAlbumData = UserAlbum.builder()
                .id(requestData.getId())
                .title(requestData.getTitle())
                .contentText(requestData.getContentText())
                .image(requestData.getImage())
                .userId(oldAlbumData.getUserId())
                .uploadDate(oldAlbumData.getUploadDate())
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
            throw new IllegalArgumentException("해당 앨범이 존재하지 않습니다:" + index);

        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new IllegalArgumentException("제약 조건 위반 오류 발생");

        } catch (Exception e) {
            throw new IllegalArgumentException("기타 예외 발생");
        }
    }


    // --------------------------------------- TEST Methods ---------------------------------------

    public List<UserAlbum> test() {
        return userAlbumRepository.findAllByTitleContains("수정된 앨범");
    }
}
