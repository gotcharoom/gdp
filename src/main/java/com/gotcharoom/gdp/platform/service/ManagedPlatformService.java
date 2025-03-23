package com.gotcharoom.gdp.platform.service;

import com.gotcharoom.gdp.platform.entity.UserPlatform;
import com.gotcharoom.gdp.platform.model.PlatformCallbackRequest;
import com.gotcharoom.gdp.platform.model.PlatformType;
import com.gotcharoom.gdp.platform.repository.PlatformRepository;
import com.gotcharoom.gdp.platform.repository.UserPlatformRepository;
import com.gotcharoom.gdp.user.entity.GdpUser;
import com.gotcharoom.gdp.user.repository.UserRepository;

public abstract class ManagedPlatformService {

    protected final PlatformRepository platformRepository;
    protected final UserPlatformRepository userPlatformRepository;

    public ManagedPlatformService(PlatformRepository platformRepository, UserPlatformRepository userPlatformRepository) {
        this.platformRepository = platformRepository;
        this.userPlatformRepository = userPlatformRepository;
    }

    public abstract PlatformType getPlatformType();

    public abstract void updatePlatformUserCert(PlatformCallbackRequest request, GdpUser user);
}
