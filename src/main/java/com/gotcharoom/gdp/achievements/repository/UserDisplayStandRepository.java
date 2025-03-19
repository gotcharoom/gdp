package com.gotcharoom.gdp.achievements.repository;

import com.gotcharoom.gdp.achievements.entity.UserDisplayStand;
import com.gotcharoom.gdp.achievements.model.response.DisplayStandGetListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDisplayStandRepository extends JpaRepository<UserDisplayStand, Long> {
    // 새 객체에 매핑해서 SELECT
    @Query("SELECT DISTINCT new com.gotcharoom.gdp.achievements.model.response.DisplayStandGetListResponse" +
            "(s.id, s.title, s.image, s.uploadDate) " +
            "FROM UserDisplayStand s JOIN s.albums ")
    Page<DisplayStandGetListResponse> findPageBy(Pageable page);
}
