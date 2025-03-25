package com.gotcharoom.gdp.achievements.repository;

import com.gotcharoom.gdp.achievements.entity.UserAlbum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAlbumRepository extends JpaRepository<UserAlbum, Long>, CustomUserAlbumRepository {
    // 새 객체에 매핑해서 SELECT
    //    @Query("SELECT DISTINCT new com.gotcharoom.gdp.achievements.model.response.AlbumGetListResponse" +
    //            "(s.id, s.title, s.image, s.uploadDate) " +
    //            "FROM UserAlbum s JOIN s.achievements ")
    //    Page<AlbumGetListResponse> findPageBy(Pageable page);

    //    List<UserAlbum> findAllByTitleContains(String title);

}
