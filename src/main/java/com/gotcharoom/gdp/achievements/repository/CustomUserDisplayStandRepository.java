package com.gotcharoom.gdp.achievements.repository;

import com.gotcharoom.gdp.achievements.model.response.DisplayStandGetListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomUserDisplayStandRepository {
    // 전시대 목록 가져오기
    Page<DisplayStandGetListResponse> findPageBy(Pageable page);
}
