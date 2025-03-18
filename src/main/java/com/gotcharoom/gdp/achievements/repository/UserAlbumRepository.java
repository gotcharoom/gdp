package com.gotcharoom.gdp.achievements.repository;

import com.gotcharoom.gdp.achievements.entity.UserAlbum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAlbumRepository extends JpaRepository<UserAlbum, Long> {

    @Query("SELECT DISTINCT s.id, s.title, s.contentText, s.image, s.userId, s.uploadDate FROM UserAlbum s JOIN s.achievements ")
    Page<UserAlbum> findPageBy(Pageable page);

}
