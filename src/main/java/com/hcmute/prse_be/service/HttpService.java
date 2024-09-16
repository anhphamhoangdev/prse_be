package com.hcmute.prse_be.service;

import com.hcmute.prse_be.util.JsonUtils;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

@Service
public class HttpService {

    private final WebClient webClient;

    private final JSONParser jsonParser;


    public HttpService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
        this.jsonParser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);

    }

    public Mono<JSONObject> sendGet(String url) {
        LogService.getgI().info("[sendGet] : " + url);
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(this::parseResponse)
                .doOnSuccess(jsonObject -> {
                    LogService.getgI().info("[sendGet] RESPONSE : " + jsonObject.toString());
                })
                .doOnError((e) -> {
                        LogService.getgI().info("[sendGet] RESPONSE : " + e.getMessage() );
                });
    }

    public Mono<JSONObject> sendGetWithRequestParams(String url, JSONObject requestParams) {
        LogService.getgI().info("[sendGetWithRequestParams] : " + url );
        LogService.getgI().info("[sendGetWithRequestParams] Request Params : " + JsonUtils.Serialize(requestParams));
        return webClient.get()
                .uri(uriBuilder -> {
                    URI baseUri = URI.create(url);
                    UriBuilder builder = UriComponentsBuilder.fromUri(baseUri);

                    requestParams.forEach((key, value) ->
                            builder.queryParam(key, value.toString()));

                    URI finalUri = builder.build();
                    LogService.getgI().info("[sendGetWithRequestParams] Final URL: " + finalUri);
                    return finalUri;
                })
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(this::parseResponse)
                .doOnSuccess(jsonObject ->
                        LogService.getgI().info("[sendGetWithRequestParams] RESPONSE : " + jsonObject.toString())
                )
                .doOnError(e -> LogService.getgI().info(e.getMessage())
                );
    }

    public Mono<JSONObject> sendGetWithRequestParamsAndHeader(String url, JSONObject requestParams, JSONObject requestHeader) {
        LogService.getgI().info("[sendGetWithRequestParamsAndHeader] : " + url );
        LogService.getgI().info("[sendGetWithRequestParamsAndHeader] Request Params : " + JsonUtils.Serialize(requestParams));
        LogService.getgI().info("[sendGetWithRequestParamsAndHeader] Request Header : " + JsonUtils.Serialize(requestHeader));
        return webClient.get()
                .uri(uriBuilder -> {
                    URI baseUri = URI.create(url);
                    UriBuilder builder = UriComponentsBuilder.fromUri(baseUri);

                    requestParams.forEach((key, value) ->
                            builder.queryParam(key, value.toString()));

                    URI finalUri = builder.build();
                    LogService.getgI().info("[sendGetWithRequestParamsAndHeader] Final URL: " + finalUri);
                    return finalUri;
                })
                .headers(httpHeaders -> {
                    requestHeader.forEach((key, value) ->
                            httpHeaders.add(key, value.toString()));
                })
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(this::parseResponse)
                .doOnSuccess(jsonObject ->  LogService.getgI().info("[sendGetWithRequestParamsAndHeader] RESPONSE : " + jsonObject.toString()))
                .doOnError(e -> LogService.getgI().info("[sendGetWithRequestParamsAndHeader] RESPONSE ERROR : " + e.getMessage()));
    }

    public Mono<JSONObject> sendPost(String url) {
        LogService.getgI().info("[sendPost] : " + url);
        return webClient.post()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(this::parseResponse)
                .doOnSuccess(jsonObject -> {
                    LogService.getgI().info("[sendPost] RESPONSE : " + jsonObject.toString());
                })
                .doOnError((e) -> {
                    LogService.getgI().info("[sendPost] RESPONSE : " + e.getMessage() );
                });
    }

    public Mono<JSONObject> sendPostWithRequestBody(String url, JSONObject requestBody)
    {
        LogService.getgI().info("[sendPostWithRequestBody] : " + url);
        LogService.getgI().info("[sendPostWithRequestBody] Request Body : " + JsonUtils.Serialize(requestBody));
        return webClient.post()
                .uri(url)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(this::parseResponse)
                .doOnSuccess(jsonObject -> {
                    LogService.getgI().info("[sendPostWithRequestBody] RESPONSE : " + jsonObject.toString());
                })
                .doOnError((e) -> {
                    LogService.getgI().info("[sendPostWithRequestBody] RESPONSE : " + e.getMessage() );
                });
    }

    public Mono<JSONObject> sendPostWithRequestBodyAndHeader(String url, JSONObject requestBody, JSONObject requestHeader)
    {
        LogService.getgI().info("[sendPostWithRequestBodyAndHeader] : " + url);
        LogService.getgI().info("[sendPostWithRequestBodyAndHeader] Request Body : " + JsonUtils.Serialize(requestBody));
        LogService.getgI().info("[sendPostWithRequestBodyAndHeader] Request Header : " + JsonUtils.Serialize(requestHeader));
        return webClient.post()
                .uri(url)
                .bodyValue(requestBody)
                .headers(httpHeaders -> {
                    requestHeader.forEach((key, value) ->
                            httpHeaders.add(key, value.toString()));
                })
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(this::parseResponse)
                .doOnSuccess(jsonObject -> {
                    LogService.getgI().info("[sendPostWithRequestBodyAndHeader] RESPONSE : " + jsonObject.toString());
                })
                .doOnError((e) -> {
                    LogService.getgI().info("[sendPostWithRequestBodyAndHeader] RESPONSE : " + e.getMessage() );
                });
    }

    private Mono<JSONObject> parseResponse(String response) {
        return Mono.fromCallable(() -> {
            try {
                Object parsedObject = jsonParser.parse(response);
                JSONObject result = new JSONObject();

                if (parsedObject instanceof JSONObject) {
                    result = (JSONObject) parsedObject;
                } else if (parsedObject instanceof JSONArray) {
                    result.put("data", parsedObject);
                } else {
                    throw new RuntimeException("Unexpected response type: " + parsedObject.getClass().getName());
                }

                return result;
            } catch (ParseException e) {
                LogService.getgI().error(e);
                throw new RuntimeException("Failed to parse JSON response", e);
            }
        });
    }

}
