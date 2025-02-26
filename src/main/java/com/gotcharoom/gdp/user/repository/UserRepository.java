package com.gotcharoom.gdp.user.repository;

import com.gotcharoom.gdp.user.entity.GdpUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<GdpUser, Long> {

    Optional<GdpUser> findById(String id);
}
