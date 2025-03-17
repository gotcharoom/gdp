package com.gotcharoom.gdp.achievements.repository;

import com.gotcharoom.gdp.achievements.entity.AlbumAchievementList;
import com.gotcharoom.gdp.achievements.entity.UserSteamAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlbumAchievementListRepository extends JpaRepository<AlbumAchievementList, Long> {

    // 쿼리 효율성을 위해 JPQL을 사용해 새 쿼리문 작성
    // JPQL에선 DB의 테이블이 아닌 엔티티명과 그 필드명을 적어줘야함
    // 현재 앨범/앨범리스트 엔티티에 양방향 직렬화를 해놨기 때문에 이 두 테이블을 JOIN FETCH하면 무한 순환 오류 발생(사용 X)
    @Query(value = "SELECT b " +
            "FROM AlbumAchievementList s " +
            "JOIN s.userAlbum a " +
            "JOIN s.achievement b " +
            "WHERE a.id = :album_id")
    List<UserSteamAchievement> findAlbumAchievementList(@Param("album_id") Long albumId);

}
