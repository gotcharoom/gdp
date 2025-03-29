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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class DisplayStandService {
    private final UserDisplayStandRepository userDisplayStandRepository;
    private final DisplayStandAlbumListRepository displayStandAlbumListRepository;

    // --------------------------------------------- CRUD ---------------------------------------------

    // 전시대 전체 목록 가져오기
    public Page<DisplayStandGetListResponse> getUserDisplayStands(int pageNo, int pageSize) {
        pageNo = pageNo == 0 || pageNo < 0 ? 0 : pageNo-1;
        System.out.println("page 넘버는 " + pageNo);
        return userDisplayStandRepository.findPageBy(PageRequest.of(pageNo, pageSize, Sort.by("id").descending()));
    }

    // 전시대 내용 가져오기 (details)
    public DisplayStandGetResponse getUserDisplayStandOne(Long id) {
        UserDisplayStand displayStand = userDisplayStandRepository.findById(id)
                .orElseThrow(() -> new EmptyResultDataAccessException("해당 ID의 전시대가 존재하지 않습니다.", 1));

        List<UserAlbum> albumList = displayStandAlbumListRepository.findAlbumList(id);

        return DisplayStandGetResponse.fromDisplayStandDetail(displayStand, albumList);
    }

    // 전시대 저장 기능
    public void saveUserDisplayStand(String userName,DisplayStandSaveRequest requestData) {
        // 전시대 수정 시 중간 테이블(DisplayStandAlbumList)도 자동으로 갱신됨 (DB Cascade 설정)

        UserDisplayStand newDisplayStandData = requestData.toEntity(userName, null);

        requestData.getAlbums().forEach(item -> {
            UserAlbum sample = UserAlbum.builder().id(item).build();

            DisplayStandAlbumList newAlbum = DisplayStandAlbumList.builder().userAlbum(sample).build();

            newDisplayStandData.addAlbum(newAlbum);
        });

        try {
            userDisplayStandRepository.save(newDisplayStandData);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("잘못된 전시대 번호 또는 앨범 번호 입니다");
        }

    }

    // 전시대 수정 기능
    public void editUserDisplayStand(String userName, Long displayStandId, DisplayStandSaveRequest requestData) {
        // 전시대 수정 시 중간 테이블(DisplayStandAlbumList)도 자동으로 갱신됨 (DB Cascade 설정)

        // 기존 데이터 조회 후 유효성 검사
        UserDisplayStand oldAlbumData = userDisplayStandRepository.findById(displayStandId)
                .orElseThrow(() -> new EmptyResultDataAccessException("해당 ID의 전시대가 존재하지 않습니다.", 1));

        isCorrectUser(userName, oldAlbumData);

        UserDisplayStand newDisplayStandData = requestData.toEntity(userName, displayStandId);

        // 전시대에 연동된 앨범 데이터를 전시대 객체에 세팅
        requestData.getAlbums().forEach(item -> {
            UserAlbum sample = UserAlbum.builder().id(item).build();

            DisplayStandAlbumList newAlbum = DisplayStandAlbumList.builder().userAlbum(sample).build();

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
    public void deleteUserDisplayStand(String userName, Long id) {
        System.out.println("id 값은 : " + id);
        try {
            // deleteById 메소드를 쓰면 Exception이 발생 안함 -> 오류 캐치를 위해 findById와 delete 메소드 사용
            UserDisplayStand displayStand = userDisplayStandRepository.findById(id)
                    .orElseThrow(() -> new EmptyResultDataAccessException(1));

            isCorrectUser(userName, displayStand);

            userDisplayStandRepository.delete(displayStand);

        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("해당 전시대가 존재하지 않습니다: " + id);

        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new IllegalArgumentException("제약 조건 위반 오류 발생");

        } catch (Exception e) {
            throw new IllegalArgumentException("기타 예외 발생");
        }
    }

    // ------------------------------------------- 권한 검사 -------------------------------------------

    public void isCorrectUser(String userName, UserDisplayStand displayStand) {
        if (!displayStand.getUserId().equals(userName)) { throw new AccessDeniedException("권한이 없습니다."); }
    }
}
