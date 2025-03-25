package com.gotcharoom.gdp.platform.repository;

import com.gotcharoom.gdp.platform.entity.Platform;
import com.gotcharoom.gdp.platform.entity.UserPlatform;
import com.gotcharoom.gdp.user.entity.GdpUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPlatformRepository extends JpaRepository<UserPlatform, Long>, CustomUserPlatformRepository {

    Optional<List<UserPlatform>> findAllByUser(GdpUser user);
    Optional<UserPlatform> findByUserAndPlatform(GdpUser user, Platform platform);
}
