package com.gotcharoom.gdp.global.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gotcharoom.gdp.global.config.WebClientConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class WebClientUtil {

    private final WebClientConfig webClientConfig;
    private final ObjectMapper objectMapper;

    /**
     * WebClient 요청 - 공통 처리 (TypeReference 사용)
     */
    private <T, V> Mono<T> sendRequest(HttpMethod method, String url, V requestBody, TypeReference<T> responseType, String target) {
        WebClient.RequestBodySpec requestSpec = webClientConfig.webClient()
                .method(method)
                .uri(url);

        // GET 요청이면 bodyValue() 호출하지 않음, 다른 요청에서는 requestBody가 null일 경우 빈 BodyInserters 사용
        if (requestBody != null && method != HttpMethod.GET) {
            requestSpec.bodyValue(requestBody);
        } else if (method != HttpMethod.GET) {
            requestSpec.body(BodyInserters.empty());
        }

        Mono<JsonNode> responseMono = requestSpec
                .retrieve()
                .bodyToMono(JsonNode.class);

        return responseMono
                .map(json -> Optional.ofNullable(target).map(json::get).orElse(json)) // target이 있으면 특정 필드 추출
                .map(resJson -> objectMapper.convertValue(resJson, new TypeReference<T>() {})); // TypeReference 적용
    }

    /**
     * WebClient 요청 - 공통 처리 (Class<T> 사용)
     */
    private <T, V> Mono<T> sendRequest(HttpMethod method, String url, V requestBody, Class<T> responseType, String target) {
        WebClient.RequestBodySpec requestSpec = webClientConfig.webClient()
                .method(method)
                .uri(url);

        // GET 요청이면 bodyValue() 호출하지 않음, 다른 요청에서는 requestBody가 null일 경우 빈 BodyInserters 사용
        if (requestBody != null && method != HttpMethod.GET) {
            requestSpec.bodyValue(requestBody);
        } else if (method != HttpMethod.GET) {
            requestSpec.body(BodyInserters.empty());
        }

        Mono<JsonNode> responseMono = requestSpec
                .retrieve()
                .bodyToMono(JsonNode.class);

        return responseMono
                .map(json -> Optional.ofNullable(target).map(json::get).orElse(json)) // target이 있으면 특정 필드 추출
                .map(resJson -> objectMapper.convertValue(resJson, responseType));
    }

    /**
     * GET 요청
     */
    public <T> T get(String url, TypeReference<T> responseType) {
        return sendRequest(HttpMethod.GET, url, null, responseType, null).block();
    }

    public <T> T get(String url, TypeReference<T> responseType, String target) {
        return sendRequest(HttpMethod.GET, url, null, responseType, target).block();
    }

    public <T> T get(String url, Class<T> responseType) {
        return sendRequest(HttpMethod.GET, url, null, responseType, null).block();
    }

    public <T> T get(String url, Class<T> responseType, String target) {
        return sendRequest(HttpMethod.GET, url, null, responseType, target).block();
    }

    /**
     * POST 요청
     */
    public <T, V> T post(String url, V requestDto, TypeReference<T> responseType) {
        return sendRequest(HttpMethod.POST, url, requestDto, responseType, null).block();
    }

    public <T, V> T post(String url, V requestDto, TypeReference<T> responseType, String target) {
        return sendRequest(HttpMethod.POST, url, requestDto, responseType, target).block();
    }

    public <T, V> T post(String url, V requestDto, Class<T> responseType) {
        return sendRequest(HttpMethod.POST, url, requestDto, responseType, null).block();
    }

    public <T, V> T post(String url, V requestDto, Class<T> responseType, String target) {
        return sendRequest(HttpMethod.POST, url, requestDto, responseType, target).block();
    }
}
