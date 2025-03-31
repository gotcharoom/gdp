package com.gotcharoom.gdp.achievements.repository;

import com.gotcharoom.gdp.achievements.model.response.AlbumGetListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomUserAlbumRepository {
    // 앨범 전체 목록
    Page<AlbumGetListResponse> findPageBy(Long displayStandId, Pageable page);

    // 앨범 제목 검색
    Page<AlbumGetListResponse> findAllByTitleContains(String title, Pageable page);
}
