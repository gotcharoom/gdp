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
public class ManagedSteamLocalService extends ManagedPlatformService {

    public ManagedSteamLocalService(
            PlatformRepository platformRepository,
            UserPlatformRepository userPlatformRepository
    ) {
        super(platformRepository, userPlatformRepository);
    }

    @Override
    public PlatformType getPlatformType() {
        return PlatformType.STEAM_LOCAL;
    }

    @Override
    public void createPlatformUserCert(PlatformCallbackRequest request, GdpUser user) {

        Platform platform = platformRepository.findByType(PlatformType.STEAM_LOCAL).orElseThrow();

        UserPlatform userPlatform = UserPlatform.builder()
                .user(user)
                .platform(platform)
                .platformUserId(request.getPlatformUserId())
                .platformUserSecret(null)
                .build();

        userPlatformRepository.save(userPlatform);
    }

    @Override
    public void updatePlatformUserCert(PlatformCallbackRequest request, GdpUser user) {
        Platform platform = platformRepository.findByType(PlatformType.STEAM).orElseThrow();

        UserPlatform userPlatform = userPlatformRepository.findByUserAndPlatform(user, platform).orElseThrow();

        UserPlatform updatedUserPlatform = userPlatform.updateUserCert(request.getPlatformUserId(), null);

        userPlatformRepository.save(updatedUserPlatform);
    }
}
