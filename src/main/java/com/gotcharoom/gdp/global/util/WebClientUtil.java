package com.gotcharoom.gdp.global.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gotcharoom.gdp.global.config.WebClientConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        if (requestBody != null && method != HttpMethod.GET) {
            requestSpec.bodyValue(requestBody);
        } else if (method != HttpMethod.GET) {
            requestSpec.body(BodyInserters.empty());
        }

        Mono<JsonNode> responseMono = requestSpec
                .retrieve()
                .bodyToMono(JsonNode.class);

        return responseMono
                .map(json -> extractTargetField(json, target)) // target이 있으면 특정 필드 추출
                .map(resJson -> objectMapper.convertValue(resJson, responseType)); // TypeReference 적용
    }

    /**
     * WebClient 요청 - 공통 처리 (Class<T> 사용)
     */
    private <T, V> Mono<T> sendRequest(HttpMethod method, String url, V requestBody, Class<T> responseType, String target) {
        WebClient.RequestBodySpec requestSpec = webClientConfig.webClient()
                .method(method)
                .uri(url);

        if (requestBody != null && method != HttpMethod.GET) {
            requestSpec.bodyValue(requestBody);
        } else if (method != HttpMethod.GET) {
            requestSpec.body(BodyInserters.empty());
        }

        Mono<JsonNode> responseMono = requestSpec
                .retrieve()
                .bodyToMono(JsonNode.class);

        return responseMono
                .map(json -> extractTargetField(json, target)) // target이 있으면 특정 필드 추출
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

    /**
     * JSON에서 target 필드 추출 ('.' 및 '[index]' 지원)
     */
    private JsonNode extractTargetField(JsonNode json, String target) {
        if (target == null || target.isEmpty()) {
            return json;
        }

        String[] keys = target.split("\\.");
        JsonNode currentNode = json;

        for (String key : keys) {
            if (currentNode == null) {
                return null;
            }

            Matcher matcher = Pattern.compile("([a-zA-Z0-9_]+)\\[([0-9]+)]").matcher(key);

            if (matcher.matches()) {
                // 키에 배열 인덱스가 있는 경우 (예: test[0])
                String fieldName = matcher.group(1); // "test"
                int index = Integer.parseInt(matcher.group(2)); // "0"

                currentNode = currentNode.get(fieldName);
                if (currentNode != null && currentNode.isArray() && currentNode.size() > index) {
                    currentNode = currentNode.get(index);
                } else {
                    return null;
                }
            } else {
                // 일반 키 (예: response, data)
                currentNode = currentNode.get(key);
            }
        }

        return currentNode;
    }
}
