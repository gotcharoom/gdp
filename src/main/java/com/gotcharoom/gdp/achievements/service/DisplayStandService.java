package com.gotcharoom.gdp.achievements.service;

import com.gotcharoom.gdp.achievements.entity.*;
import com.gotcharoom.gdp.achievements.model.request.DisplayStandSaveRequest;
import com.gotcharoom.gdp.achievements.model.response.DisplayStandGetListResponse;
import com.gotcharoom.gdp.achievements.model.response.DisplayStandGetResponse;
import com.gotcharoom.gdp.achievements.repository.DisplayStandAlbumListRepository;
import com.gotcharoom.gdp.achievements.repository.UserDisplayStandRepository;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class DisplayStandService {
    private final UserDisplayStandRepository userDisplayStandRepository;
    private final DisplayStandAlbumListRepository displayStandAlbumListRepository;

    // 전시대 전체 목록 가져오기
    public Page<DisplayStandGetListResponse> getUserDisplayStands(int pageNo, int pageSize) {
        return userDisplayStandRepository.findPageBy(PageRequest.of(pageNo, pageSize, Sort.by("id").descending()));
    }

    // 전시대 내용 가져오기 (details)
    public DisplayStandGetResponse getUserDisplayStandOne(Long index) {
        UserDisplayStand displayStand = userDisplayStandRepository.findById(index)
                .orElseThrow(() -> new EmptyResultDataAccessException("해당 ID의 전시대가 존재하지 않습니다.", 1));

        List<UserAlbum> albumList = displayStandAlbumListRepository.findAlbumList(index);

        return DisplayStandGetResponse.builder()
                .id(displayStand.getId())
                .title(displayStand.getTitle())
                .contentText(displayStand.getContentText())
                .image(displayStand.getImage())
                .albums(albumList)
                .build();
    }

    // 전시대 저장 기능
    public void saveUserDisplayStand(DisplayStandSaveRequest requestData) {
        // 중간 테이블(DisplayStandAlbumList)도 자동으로 갱신됨

        UserDisplayStand newDisplayStandData = UserDisplayStand.builder()
                .title(requestData.getTitle())
                .contentText(requestData.getContentText())
                .image(requestData.getImage())
                .userId("test")
                .build();

        requestData.getAlbums().forEach(item -> {
            UserAlbum sample = UserAlbum.builder()
                    .id(item)
                    .build();

            DisplayStandAlbumList newAlbum = DisplayStandAlbumList.builder()
                    .userAlbum(sample)
                    .build();

            newDisplayStandData.addAlbum(newAlbum);
        });

        try {
            userDisplayStandRepository.save(newDisplayStandData);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("잘못된 전시대 번호 또는 앨범 번호 입니다");
        }

    }

    // 전시대 수정 기능
    public void editUserDisplayStand(Long displayStandId, DisplayStandSaveRequest requestData) {
        // 중간 테이블(DisplayStandAlbumList)도 자동으로 갱신됨

        // 생성 날짜 불변을 위해 기존 데이터 조회
        UserDisplayStand oldAlbumData = userDisplayStandRepository.findById(displayStandId)
                .orElseThrow(() -> new EmptyResultDataAccessException("해당 ID의 전시대가 존재하지 않습니다.", 1));

        UserDisplayStand newDisplayStandData = UserDisplayStand.builder()
                .id(displayStandId)
                .title(requestData.getTitle())
                .contentText(requestData.getContentText())
                .image(requestData.getImage())
                .userId(oldAlbumData.getUserId())
                .uploadDate(oldAlbumData.getUploadDate())
                .build();

        requestData.getAlbums().forEach(item -> {
            UserAlbum sample = UserAlbum.builder()
                    .id(item)
                    .build();

            DisplayStandAlbumList newAlbum = DisplayStandAlbumList.builder()
                    .userAlbum(sample)
                    .build();

            newDisplayStandData.addAlbum(newAlbum);
        });

        try {
            userDisplayStandRepository.save(newDisplayStandData);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("잘못된 전시대 번호 또는 앨범 번호 입니다");
        }

    }

    // 전시대 삭제 기능
    // @Transactional
    public void deleteUserDisplayStand(Long index) {
        System.out.println("index 값은 : " + index);
        try {
            // deleteById 메소드를 쓰면 Exception이 발생 안함 -> 오류 캐치를 위해 findById와 delete 메소드 사용
            UserDisplayStand displayStand = userDisplayStandRepository.findById(index)
                    .orElseThrow(() -> new EmptyResultDataAccessException(1));
            userDisplayStandRepository.delete(displayStand);

        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("해당 전시대가 존재하지 않습니다: " + index);

        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new IllegalArgumentException("제약 조건 위반 오류 발생");

        } catch (Exception e) {
            throw new IllegalArgumentException("기타 예외 발생");
        }
    }
}
