package com.gotcharoom.gdp.global.util;

import com.gotcharoom.gdp.global.config.WebClientConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebClientUtil {

    private final WebClientConfig webClientConfig;

    public <T> T get(String url, Class<T> responseDtoClass) {
        return webClientConfig.webClient().method(HttpMethod.GET)
                .uri(url)
                .retrieve()
                .bodyToMono(responseDtoClass)
                .block();
    }

    public <T> T get(String url, ParameterizedTypeReference<T> responseType) {
        return webClientConfig.webClient().method(HttpMethod.GET)
                .uri(url)
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }


    public <T, V> T post(String url, V requestDto, Class<T> responseDtoClass) {
        return webClientConfig.webClient().method(HttpMethod.POST)
                .uri(url)
                .bodyValue(requestDto)
                .retrieve()
                .bodyToMono(responseDtoClass)
                .block();
    }

    public <T, V> T post(String url, V requestDto, ParameterizedTypeReference<T> responseType) {
        return webClientConfig.webClient().method(HttpMethod.POST)
                .uri(url)
                .bodyValue(requestDto)
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }
}