package com.gotcharoom.gdp.platform.service;

import com.gotcharoom.gdp.global.util.JwtUtil;
import com.gotcharoom.gdp.global.util.UserUtil;
import com.gotcharoom.gdp.platform.entity.Platform;
import com.gotcharoom.gdp.platform.entity.UserPlatform;
import com.gotcharoom.gdp.platform.model.*;
import com.gotcharoom.gdp.platform.repository.PlatformRepository;
import com.gotcharoom.gdp.platform.repository.UserPlatformRepository;
import com.gotcharoom.gdp.user.entity.GdpUser;
import com.gotcharoom.gdp.user.model.UserDetailPlatform;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PlatformService {
    private final PlatformRepository platformRepository;
    private final UserPlatformRepository userPlatformRepository;
    private final UserUtil userUtil;
    private final JwtUtil jwtUtil;
    private final PlatformServiceRouter platformServiceRouter;

    public PlatformService(
            PlatformRepository platformRepository,
            UserPlatformRepository userPlatformRepository,
            UserUtil userUtil,
            JwtUtil jwtUtil,
            PlatformServiceRouter platformServiceRouter
    ) {
        this.platformRepository = platformRepository;
        this.userPlatformRepository = userPlatformRepository;
        this.userUtil = userUtil;
        this.jwtUtil = jwtUtil;
        this.platformServiceRouter = platformServiceRouter;
    }

    public void createPlatform(PlatformCreateRequest request) {
        Platform platform = Platform.builder()
                .name(request.getName())
                .type(request.getPlatformType())
                .url(request.getUrl())
                .useYn(PlatformUseYn.Y)
                .build();

        platformRepository.save(platform);
    }

    public void deletePlatform(Long id) {
        Platform platform = platformRepository.findById(id).orElseThrow();

        platformRepository.delete(platform);
    }

    public void modifyPlatform(PlatformModifyRequest request) {
        Platform platform = platformRepository.findById(request.getId()).orElseThrow();
        Platform updatedPlatform = platform.updatePlatform(request.getName(), request.getUrl(), request.getUseYn());

        platformRepository.save(updatedPlatform);
    }

    public List<PlatformResponse> getPlatforms() {

        List<Platform> platforms =  platformRepository.findAllByUseYn(PlatformUseYn.Y).orElseThrow();
        return platforms.stream().map(PlatformResponse::fromEntity).toList();
    }

    public List<UserPlatform> getUserPlatforms(GdpUser user) {

        return userPlatformRepository.findAllByUser(user).orElse(new ArrayList<>());
    }

    public List<UserDetailPlatform> getUserDetailPlatforms(Long uid) {

        return userPlatformRepository.findActiveUserPlatformsByUserId(uid).orElse(new ArrayList<>());
    }

    public void connectPlatform(PlatformType platformType, PlatformCallbackRequest request, HttpServletResponse response) {
        GdpUser user = userUtil.getUserFromContext();

        ManagedPlatformService managedPlatformService = platformServiceRouter.getServiceImplements(platformType);

        managedPlatformService.createPlatformUserCert(request, user);

        setConnectionCookie(response);
    }

    private void setConnectionCookie(HttpServletResponse response) {
        jwtUtil.setPlatformConnectionCookie(response);
    }

    public void removeConnectionCookie(HttpServletResponse response) {
        jwtUtil.removePlatformConnectionCookie(response);
    }
}
