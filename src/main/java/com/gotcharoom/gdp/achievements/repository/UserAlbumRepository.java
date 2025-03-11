package com.gotcharoom.gdp.achievements.repository;

import com.gotcharoom.gdp.achievements.entity.UserAlbum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAlbumRepository extends JpaRepository<UserAlbum, Long> {
}
