package com.gotcharoom.gdp.platform.service;

import com.gotcharoom.gdp.platform.entity.Platform;
import com.gotcharoom.gdp.platform.entity.UserPlatform;
import com.gotcharoom.gdp.platform.model.PlatformCallbackRequest;
import com.gotcharoom.gdp.platform.model.PlatformType;
import com.gotcharoom.gdp.platform.repository.PlatformRepository;
import com.gotcharoom.gdp.platform.repository.UserPlatformRepository;
import com.gotcharoom.gdp.user.entity.GdpUser;
import org.springframework.stereotype.Service;

@Service
public class ManagedSteamService extends ManagedPlatformService {

    public ManagedSteamService(
            PlatformRepository platformRepository,
            UserPlatformRepository userPlatformRepository
    ) {
        super(platformRepository, userPlatformRepository);
    }

    @Override
    public PlatformType getPlatformType() {
        return PlatformType.STEAM;
    }

    @Override
    public void updatePlatformUserCert(PlatformCallbackRequest request, GdpUser user) {

        Platform platform = platformRepository.findByType(PlatformType.STEAM).orElseThrow();

        UserPlatform userPlatform = userPlatformRepository.findByUserAndPlatform(user, platform).orElse(
                UserPlatform.builder()
                        .user(user)
                        .platform(platform)
                        .platformUserId(request.getPlatformUserId())
                        .platformUserSecret(null)
                        .build()
        );

        userPlatformRepository.save(userPlatform);
    }
}
