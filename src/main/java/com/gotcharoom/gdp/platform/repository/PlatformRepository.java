package com.gotcharoom.gdp.platform.repository;

import com.gotcharoom.gdp.platform.entity.Platform;
import com.gotcharoom.gdp.platform.model.PlatformType;
import com.gotcharoom.gdp.platform.model.PlatformUseYn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlatformRepository extends JpaRepository<Platform, Long> {

    Optional<List<Platform>> findAllByUseYn(PlatformUseYn useYn);
    Optional<Platform> findByType(PlatformType type);
}
