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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PlatformService {
    private static final Logger logger = LoggerFactory.getLogger(PlatformService.class);

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
        logger.info("Creating platform with name: {}", request.getName());
        Platform platform = Platform.builder()
                .name(request.getName())
                .type(request.getPlatformType())
                .url(request.getUrl())
                .useYn(PlatformUseYn.Y)
                .build();

        platformRepository.save(platform);
        logger.info("Platform created successfully: {}", platform.getId());
    }

    public void deletePlatform(Long id) {
        logger.info("Deleting platform with id: {}", id);
        Platform platform = platformRepository.findById(id).orElseThrow();

        platformRepository.delete(platform);
        logger.info("Platform deleted successfully: {}", id);
    }

    public void modifyPlatform(PlatformModifyRequest request) {
        logger.info("Modifying platform with id: {}", request.getId());
        Platform platform = platformRepository.findById(request.getId()).orElseThrow();
        Platform updatedPlatform = platform.updatePlatform(request.getName(), request.getUrl(), request.getUseYn());

        platformRepository.save(updatedPlatform);
        logger.info("Platform updated successfully: {}", updatedPlatform.getId());
    }

    public List<PlatformResponse> getPlatforms() {
        logger.info("Fetching all active platforms");
        List<Platform> platforms =  platformRepository.findAllByUseYn(PlatformUseYn.Y).orElseThrow();
        logger.info("Found {} active platforms", platforms.size());
        return platforms.stream().map(PlatformResponse::fromEntity).toList();
    }

    public List<UserPlatform> getUserPlatforms(GdpUser user) {
        logger.info("Fetching platforms connected by user: {}", user.getId());
        List<UserPlatform> userPlatforms = userPlatformRepository.findAllByUser(user).orElse(new ArrayList<>());
        logger.info("Found {} platforms for user: {}", userPlatforms.size(), user.getId());
        return userPlatforms;
    }

    public List<UserDetailPlatform> getUserDetailPlatforms(Long uid) {
        logger.info("Fetching detailed platform info for user id: {}", uid);
        List<UserDetailPlatform> detailPlatforms = userPlatformRepository.findActiveUserPlatformsByUserId(uid).orElse(new ArrayList<>());
        logger.info("Found {} detailed platforms for user id: {}", detailPlatforms.size(), uid);
        return detailPlatforms;
    }

    public void connectPlatform(PlatformType platformType, PlatformCallbackRequest request, HttpServletResponse response) {
        logger.info("Connecting platform of type: {}", platformType);
        GdpUser user = userUtil.getUserFromContext();
        ManagedPlatformService managedPlatformService = platformServiceRouter.getServiceImplements(platformType);

        managedPlatformService.createPlatformUserCert(request, user);
        logger.info("Platform connected successfully for user id: {}", user.getId());

        setConnectionCookie(response);
    }

    private void setConnectionCookie(HttpServletResponse response) {
        logger.info("Setting platform connection cookie");
        jwtUtil.setPlatformConnectionCookie(response);
    }

    public void removeConnectionCookie(HttpServletResponse response) {
        logger.info("Removing platform connection cookie");
        jwtUtil.removePlatformConnectionCookie(response);
    }
}
