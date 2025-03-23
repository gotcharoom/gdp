package com.gotcharoom.gdp.platform.repository;

import com.gotcharoom.gdp.platform.entity.Platform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlatformRepository extends JpaRepository<Platform, Long> {
}
