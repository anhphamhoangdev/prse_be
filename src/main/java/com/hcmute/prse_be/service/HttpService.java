package com.hcmute.prse_be.service;

import com.hcmute.prse_be.util.JsonUtils;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class HttpService {
    private final WebClient webClient;

    public HttpService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    /**
     * Gửi GET request đơn giản
     */
    public Mono<JSONObject> sendGet(String url) {
        LogService.getgI().info("[sendGet] : " + url);

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(this::parseResponse)
                .doOnSuccess(jsonObject ->
                        LogService.getgI().info("[sendGet] RESPONSE : " + jsonObject.toString()))
                .doOnError(e ->
                        LogService.getgI().info("[sendGet] ERROR : " + e.getMessage()));
    }

    /**
     * Gửi GET request với params
     */
    public Mono<JSONObject> sendGetWithParams(String url, JSONObject params) {
        LogService.getgI().info("[sendGetWithParams] : " + url);
        LogService.getgI().info("[sendGetWithParams] Request Params : " + JsonUtils.Serialize(params));

        String finalUrl = buildUrlWithParams(url, params);
        LogService.getgI().info("[sendGetWithParams] Final URL: " + finalUrl);

        return webClient.get()
                .uri(finalUrl)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(this::parseResponse)
                .doOnSuccess(jsonObject ->
                        LogService.getgI().info("[sendGetWithParams] RESPONSE : " + jsonObject.toString()))
                .doOnError(e ->
                        LogService.getgI().info("[sendGetWithParams] ERROR : " + e.getMessage()));
    }

    /**
     * Gửi GET request với params và headers
     */
    public Mono<JSONObject> sendGetWithParamsAndHeaders(String url, JSONObject params, JSONObject headers) {
        LogService.getgI().info("[sendGetWithParamsAndHeaders] : " + url);
        LogService.getgI().info("[sendGetWithParamsAndHeaders] Request Params : " + JsonUtils.Serialize(params));
        LogService.getgI().info("[sendGetWithParamsAndHeaders] Request Headers : " + JsonUtils.Serialize(headers));

        String finalUrl = buildUrlWithParams(url, params);
        LogService.getgI().info("[sendGetWithParamsAndHeaders] Final URL: " + finalUrl);

        return webClient.get()
                .uri(finalUrl)
                .headers(httpHeaders -> addHeaders(httpHeaders, headers))
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(this::parseResponse)
                .doOnSuccess(jsonObject ->
                        LogService.getgI().info("[sendGetWithParamsAndHeaders] RESPONSE : " + jsonObject.toString()))
                .doOnError(e ->
                        LogService.getgI().info("[sendGetWithParamsAndHeaders] ERROR : " + e.getMessage()));
    }

    /**
     * Gửi POST request với body
     */
    public Mono<JSONObject> sendPost(String url, JSONObject requestBody) {
        LogService.getgI().info("[sendPost] : " + url);
        LogService.getgI().info("[sendPost] Request Body : " + JsonUtils.Serialize(requestBody));

        return webClient.post()
                .uri(url)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(this::parseResponse)
                .map(response -> {
                    LogService.getgI().info("[sendPostWithRequestBody] RESPONSE : " + response.toString());
                    return response;
                })
                .onErrorMap(e -> {
                    LogService.getgI().info("[sendPostWithRequestBody] ERROR : " + e.getMessage());
                    return e;
                });
    }

    /**
     * Gửi POST request với body và headers
     */
    public Mono<JSONObject> sendPostWithHeaders(String url, JSONObject requestBody, JSONObject headers) {
        LogService.getgI().info("[sendPostWithHeaders] : " + url);
        LogService.getgI().info("[sendPostWithHeaders] Request Body : " + JsonUtils.Serialize(requestBody));
        LogService.getgI().info("[sendPostWithHeaders] Request Headers : " + JsonUtils.Serialize(headers));

        return webClient.post()
                .uri(url)
                .bodyValue(requestBody)
                .headers(httpHeaders -> addHeaders(httpHeaders, headers))
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(this::parseResponse)
                .doOnSuccess(jsonObject ->
                        LogService.getgI().info("[sendPostWithHeaders] RESPONSE : " + jsonObject.toString()))
                .doOnError(e ->
                        LogService.getgI().info("[sendPostWithHeaders] ERROR : " + e.getMessage()));
    }

    /**
     * Helper method để build URL với params
     */
    private String buildUrlWithParams(String baseUrl, JSONObject params) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl);

        params.forEach((key, value) ->
                builder.queryParam(key, value.toString()));

        return builder.build().toUriString();
    }

    /**
     * Helper method để thêm headers
     */
    private void addHeaders(HttpHeaders httpHeaders, JSONObject headers) {
        headers.forEach((key, value) ->
                httpHeaders.add(key, value.toString()));
    }

    /**
     * Helper method để parse response
     */
    /**
     * Helper method để parse response
     */
    private Mono<JSONObject> parseResponse(String response) {
        try {
            if (response == null || response.trim().isEmpty()) {
                LogService.getgI().info("[parseResponse] Empty response");
                return Mono.error(new RuntimeException("Empty response"));
            }

            JSONParser parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
            Object parsedObj = parser.parse(response);

            if (parsedObj instanceof Map) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.putAll((Map<String, ?>) parsedObj);
                return Mono.just(jsonObject);
            }

            // Wrap non-object responses
            JSONObject wrappedResponse = new JSONObject();
            wrappedResponse.put("data", parsedObj);
            return Mono.just(wrappedResponse);

        } catch (Exception e) {
            LogService.getgI().info("[parseResponse] Error: " + e.getMessage() + " | Response: " + response);
            return Mono.error(new RuntimeException("Failed to parse response: " + e.getMessage()));
        }
    }
}