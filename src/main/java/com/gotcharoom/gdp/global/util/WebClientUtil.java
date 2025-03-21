package com.gotcharoom.gdp.global.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gotcharoom.gdp.global.config.WebClientConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebClientUtil {

    private final WebClientConfig webClientConfig;
    private final ObjectMapper objectMapper;

    private <T, V> Mono<T> sendRequest(HttpMethod method, String url, V requestBody, TypeReference<T> responseType, String target) {
        log.info("Sending {} request to URL: {}, target: {}", method, url, target);
        WebClient.RequestBodySpec requestSpec = webClientConfig.webClient()
                .method(method)
                .uri(url);

        if (requestBody != null && method != HttpMethod.GET) {
            requestSpec.bodyValue(requestBody);
            log.info("Request body: {}", requestBody);
        } else if (method != HttpMethod.GET) {
            requestSpec.body(BodyInserters.empty());
        }

        return requestSpec
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(json -> {
                    log.info("Received response: {}", json);
                    return extractAndConvert(json, target, responseType);
                });
    }

    private <T, V> Mono<T> sendRequest(HttpMethod method, String url, V requestBody, Class<T> responseType, String target) {
        return sendRequest(method, url, requestBody, responseType, target, null);
    }

    private <T, V> Mono<T> sendRequest(HttpMethod method, String url, V requestBody, Class<T> responseType, String target, String contentType) {
        log.info("Sending {} request to URL: {}, target: {}, contentType: {}", method, url, target, contentType);
        WebClient.RequestBodySpec requestSpec = webClientConfig.webClient()
                .method(method)
                .uri(url);

        if (requestBody != null && method != HttpMethod.GET) {
            requestSpec.bodyValue(requestBody);
            log.info("Request body: {}", requestBody);
        } else if (method != HttpMethod.GET) {
            requestSpec.body(BodyInserters.empty());
        }

        if (contentType != null) {
            requestSpec.header("Content-Type", contentType);
        }

        return requestSpec
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(json -> {
                    log.info("Received response: {}", json);
                    return extractAndConvert(json, target, responseType);
                });
    }

    // GET
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

    // POST
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

    // PUT
    public <T, V> T put(String url, V requestBody, TypeReference<T> responseType) {
        return sendRequest(HttpMethod.PUT, url, requestBody, responseType, null).block();
    }

    public <T, V> T put(String url, V requestBody, TypeReference<T> responseType, String target) {
        return sendRequest(HttpMethod.PUT, url, requestBody, responseType, target).block();
    }

    public <T, V> T put(String url, V requestBody, Class<T> responseType) {
        return sendRequest(HttpMethod.PUT, url, requestBody, responseType, null, getContentType(requestBody)).block();
    }

    public <T, V> T put(String url, V requestBody, Class<T> responseType, String target) {
        return sendRequest(HttpMethod.PUT, url, requestBody, responseType, target, getContentType(requestBody)).block();
    }

    // DELETE
    public <T> T delete(String url, TypeReference<T> responseType) {
        return sendRequest(HttpMethod.DELETE, url, null, responseType, null).block();
    }

    public <T> T delete(String url, TypeReference<T> responseType, String target) {
        return sendRequest(HttpMethod.DELETE, url, null, responseType, target).block();
    }

    public <T> T delete(String url, Class<T> responseType) {
        return sendRequest(HttpMethod.DELETE, url, null, responseType, null).block();
    }

    public <T> T delete(String url, Class<T> responseType, String target) {
        return sendRequest(HttpMethod.DELETE, url, null, responseType, target).block();
    }

    // JSON field extractor
    private JsonNode extractTargetField(JsonNode json, String target) {
        if (target == null || target.isEmpty()) {
            return json;
        }

        log.info("Extracting target field: {}", target);
        String[] keys = target.split("\\.");
        JsonNode currentNode = json;

        for (String key : keys) {
            if (currentNode == null) return null;

            Matcher matcher = Pattern.compile("([a-zA-Z0-9_]+)\\[([0-9]+)]").matcher(key);
            if (matcher.matches()) {
                String fieldName = matcher.group(1);
                int index = Integer.parseInt(matcher.group(2));
                currentNode = currentNode.get(fieldName);
                if (currentNode != null && currentNode.isArray() && currentNode.size() > index) {
                    currentNode = currentNode.get(index);
                } else {
                    return null;
                }
            } else {
                currentNode = currentNode.get(key);
            }
        }

        return currentNode;
    }

    // JSON convert
    private <T> T extractAndConvert(JsonNode json, String target, Class<T> responseType) {
        try {
            if (target != null && !target.isEmpty()) {
                json = extractTargetField(json, target);
            }
            if (json == null || json.isNull()) {
                return null;
            }
            if (responseType == String.class) {
                return responseType.cast(objectMapper.writeValueAsString(json));
            }
            return objectMapper.convertValue(json, responseType);
        } catch (Exception e) {
            log.error("Error converting response to type {}: {}", responseType.getSimpleName(), e.getMessage(), e);
            return null;
        }
    }

    private <T> T extractAndConvert(JsonNode json, String target, TypeReference<T> responseType) {
        try {
            if (target != null && !target.isEmpty()) {
                json = extractTargetField(json, target);
            }
            if (json == null || json.isNull()) {
                return null;
            }
            return objectMapper.convertValue(json, responseType);
        } catch (Exception e) {
            log.error("Error converting response to type {}: {}", responseType.getType(), e.getMessage(), e);
            return null;
        }
    }

    private <V> String getContentType(V requestBody) {
        if (requestBody instanceof MultipartFile multipartFile) {
            return multipartFile.getContentType() != null ? multipartFile.getContentType() : MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
        return MediaType.APPLICATION_OCTET_STREAM_VALUE;
    }

    public boolean existsByHead(String url) {
        try {
            log.info("Checking HEAD for URL: {}", url);
            ResponseEntity<Void> response = webClientConfig.webClient()
                    .method(HttpMethod.HEAD)
                    .uri(url)
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            HttpHeaders headers = response.getHeaders();
            String contentType = headers.getContentType().toString();
            log.info("HEAD response content-type: {}", contentType);

            if (!contentType.startsWith("image/") && !contentType.equals("application/octet-stream")) {
                log.warn("Invalid content type: {}", contentType);
                throw new RuntimeException("This fileDir is not a file");
            }

            return true;
        } catch (Exception e) {
            log.error("HEAD request failed for URL: {} - {}", url, e.getMessage());
            return false;
        }
    }
}
