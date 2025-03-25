package com.gotcharoom.gdp.platform.service;

import com.gotcharoom.gdp.platform.model.PlatformType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PlatformServiceRouter {

    private final Map<PlatformType, ManagedPlatformService> platformServiceMap;

    public PlatformServiceRouter(List<ManagedPlatformService> platformServiceMap) {
        // Function.identity == 자기 자신 == ManagedPlatformService의 구현체를 의미
        // 즉, Map<PlatformType, ManagedPlatformService>
        this.platformServiceMap = platformServiceMap.stream().collect(Collectors.toMap(ManagedPlatformService::getPlatformType, Function.identity()));
    }

    public ManagedPlatformService getServiceImplements(PlatformType platformType) {
        return platformServiceMap.get(platformType);
    }
}
