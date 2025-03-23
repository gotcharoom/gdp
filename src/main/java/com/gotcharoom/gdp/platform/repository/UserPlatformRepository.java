package com.gotcharoom.gdp.platform.repository;

import com.gotcharoom.gdp.platform.entity.UserPlatform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPlatformRepository extends JpaRepository<UserPlatform, Long> {
}
