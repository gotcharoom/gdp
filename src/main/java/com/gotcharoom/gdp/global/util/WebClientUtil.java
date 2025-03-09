package com.gotcharoom.gdp.global.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gotcharoom.gdp.global.config.WebClientConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class WebClientUtil {

    private final WebClientConfig webClientConfig;
    private final ObjectMapper objectMapper;

    /**
     * TypeReference를 ParameterizedTypeReference로 변환
     */
    private <T> ParameterizedTypeReference<T> toParameterizedTypeReference(TypeReference<T> typeRef) {
        return new ParameterizedTypeReference<T>() {};
    }

    /**
     * WebClient 요청 - ParameterizedTypeReference 사용
     */
    private <T, V> Mono<T> sendRequest(HttpMethod method, String url, V requestBody, TypeReference<T> responseType) {
        return webClientConfig.webClient()
                .method(method)
                .uri(url)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(toParameterizedTypeReference(responseType)); // 변환된 타입 사용
    }

    /**
     * WebClient 요청 - 특정 JSON 필드(target)만 추출 후 변환
     */
    private <T, V> Mono<T> sendRequest(HttpMethod method, String url, V requestBody, TypeReference<T> responseType, String target) {
        return webClientConfig.webClient()
                .method(method)
                .uri(url)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(json -> json.get(target))
                .map(resJson -> objectMapper.convertValue(resJson, responseType));
    }

    /**
     * WebClient 요청 - Class<T> 사용
     */
    private <T, V> Mono<T> sendRequest(HttpMethod method, String url, V requestBody, Class<T> responseType) {
        return webClientConfig.webClient()
                .method(method)
                .uri(url)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(responseType);
    }

    /**
     * WebClient 요청 - 특정 JSON 필드(target)만 추출 후 Class<T> 변환
     */
    private <T, V> Mono<T> sendRequest(HttpMethod method, String url, V requestBody, Class<T> responseType, String target) {
        return webClientConfig.webClient()
                .method(method)
                .uri(url)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(json -> json.get(target))
                .map(resJson -> objectMapper.convertValue(resJson, responseType));
    }

    /**
     * GET 요청 - 전체 응답(JSON 전체)를 객체로 변환
     */
    public <T> T get(String url, Class<T> responseDtoClass) {
        return sendRequest(HttpMethod.GET, url, null, responseDtoClass).block();
    }

    public <T> T get(String url, TypeReference<T> responseType) {
        return sendRequest(HttpMethod.GET, url, null, responseType).block();
    }

    /**
     * GET 요청 - 특정 필드(target)만 추출 후 변환
     */
    public <T> T get(String url, Class<T> responseDtoClass, String target) {
        return sendRequest(HttpMethod.GET, url, null, responseDtoClass, target).block();
    }

    public <T> T get(String url, TypeReference<T> responseType, String target) {
        return sendRequest(HttpMethod.GET, url, null, responseType, target).block();
    }

    /**
     * POST 요청 - 전체 응답(JSON 전체)를 객체로 변환
     */
    public <T, V> T post(String url, V requestDto, Class<T> responseDtoClass) {
        return sendRequest(HttpMethod.POST, url, requestDto, responseDtoClass).block();
    }

    public <T, V> T post(String url, V requestDto, TypeReference<T> responseType) {
        return sendRequest(HttpMethod.POST, url, requestDto, responseType).block();
    }

    /**
     * POST 요청 - 특정 필드(target)만 추출 후 변환
     */
    public <T, V> T post(String url, V requestDto, Class<T> responseDtoClass, String target) {
        return sendRequest(HttpMethod.POST, url, requestDto, responseDtoClass, target).block();
    }

    public <T, V> T post(String url, V requestDto, TypeReference<T> responseType, String target) {
        return sendRequest(HttpMethod.POST, url, requestDto, responseType, target).block();
    }
}
