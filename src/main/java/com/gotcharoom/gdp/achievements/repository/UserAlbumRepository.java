package com.gotcharoom.gdp.achievements.repository;

import com.gotcharoom.gdp.achievements.entity.UserAlbum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAlbumRepository extends JpaRepository<UserAlbum, Long> {

//    @Query("SELECT o FROM Orders o JOIN FETCH o.orderProducts op JOIN FETCH op.product WHERE o.id = :id")
//    Optional<Orders> findOrderWithProducts(@Param("id") Long id);

}
