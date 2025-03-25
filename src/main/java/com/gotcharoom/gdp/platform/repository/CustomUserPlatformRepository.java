package com.gotcharoom.gdp.platform.repository;

import com.gotcharoom.gdp.platform.entity.UserPlatform;
import com.gotcharoom.gdp.user.model.UserDetailPlatform;

import java.util.List;
import java.util.Optional;

public interface CustomUserPlatformRepository {

    Optional<List<UserDetailPlatform>> findActiveUserPlatformsByUserId(Long uid);
}
