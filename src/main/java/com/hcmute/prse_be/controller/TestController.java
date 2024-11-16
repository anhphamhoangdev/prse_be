package com.hcmute.prse_be.controller;

import com.hcmute.prse_be.response.Response;
import com.hcmute.prse_be.service.HttpService;
import com.hcmute.prse_be.service.LogService;
import com.hcmute.prse_be.util.ConvertUtils;
import net.minidev.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class TestController {

    private final HttpService httpService;


    public TestController(HttpService httpService) {
        this.httpService = httpService;
    }

    // sendGet(String url)
    @GetMapping("testGet")
    public JSONObject doTestGet()
    {
        JSONObject response = new JSONObject();
        response.put("message", "sendGet(String url) : SUCCESS");
        return response;
    }

    // sendGetWithRequestParams(String url, JSONObject requestParams)
    @GetMapping("testGetWithRequestParams")
    public JSONObject doGetWithRequestParams(@RequestParam Map<String, String> params)
    {
        JSONObject jsonResponse = new JSONObject();
        try {

            String language = ConvertUtils.toString(params.get("language"));
            String id = ConvertUtils.toString(params.get("id"));
            if(language == null || language.isEmpty() || id == null || id.isEmpty() )
            {
                return Response.error("LANGUAGE OR ID IS MISSING");
            }
            jsonResponse.put("message", "sendGetWithRequestParams(String url, JSONObject requestParams) : SUCCESS");
            return jsonResponse;
        }catch (Exception e)
        {
            return Response.error(e.getMessage());
        }
    }

    @GetMapping("testGetWithRequestParamsAndHeader")
    public JSONObject doGetWithRequestParamsAndHeader(@RequestHeader("accept_token") String accept_token,
                                                      @RequestParam Map<String, String> params)
    {
        JSONObject jsonResponse = new JSONObject();
        try {
            String language = ConvertUtils.toString(params.get("language"));
            String id = ConvertUtils.toString(params.get("id"));
            if(language == null || language.isEmpty() || id == null || id.isEmpty() || accept_token == null || accept_token.isEmpty())
            {
                return Response.error("LANGUAGE OR ID OR ACCEPT_TOKEN IS MISSING");
            }
            jsonResponse.put("message", "sendGetWithRequestParamsAndHeader(String url, JSONObject requestParams, JSONObject requestHeader) : SUCCESS");
            jsonResponse.put("accept_token", accept_token);
            return jsonResponse;
        }catch (Exception e)
        {
            return Response.error(e.getMessage());
        }
    }

    @PostMapping("testPost")
    public JSONObject doPost()
    {
        JSONObject response = new JSONObject();
        response.put("message", "testPost(String url) : SUCCESS");
        return response;
    }

    @PostMapping("testPostWithRequestBody")
    public JSONObject doPostWithRequestBody(@RequestBody Map<String, String> params)
    {
        JSONObject jsonResponse = new JSONObject();
        try {
            String language = ConvertUtils.toString(params.get("language"));
            String id = ConvertUtils.toString(params.get("id"));
            if(language == null || language.isEmpty() || id == null || id.isEmpty() )
            {
                return Response.error("LANGUAGE OR ID IS MISSING");
            }
            jsonResponse.put("message", "testPostWithRequestBody : SUCCESS");
            return jsonResponse;
        }catch (Exception e)
        {
            return Response.error(e.getMessage());
        }
    }

    @PostMapping("testPostWithRequestBodyAndHeader")
    public JSONObject doPostWithRequestBodyAndHeader(@RequestHeader("accept_token") String accept_token,
                                                      @RequestBody Map<String, String> params)
    {
        JSONObject jsonResponse = new JSONObject();
        try {
            String language = ConvertUtils.toString(params.get("language"));
            String id = ConvertUtils.toString(params.get("id"));
            if(language == null || language.isEmpty() || id == null || id.isEmpty() || accept_token == null || accept_token.isEmpty())
            {
                return Response.error("LANGUAGE OR ID OR ACCEPT_TOKEN IS MISSING");
            }
            jsonResponse.put("message", "testPostWithRequestBodyAndHeader : SUCCESS");
            jsonResponse.put("accept_token", accept_token);
            return jsonResponse;
        }catch (Exception e)
        {
            return Response.error(e.getMessage());
        }
    }


    @PostMapping("sendGet")
    public JSONObject sendGet(@RequestBody Map<String, String> params) {
        JSONObject response = httpService.sendGet(params.get("url")).block();
        return response;
    }

    @PostMapping("sendGetWithRequestParams")
    public JSONObject sendGetWithRequestParams(@RequestBody JSONObject params) {
        String url = ConvertUtils.toString(params.get("url"));
        params.remove("url");
        LogService.getgI().info("url " + url );
        JSONObject response = httpService.sendGetWithRequestParams(url, params).block();
        return response;
    }

    @PostMapping("sendGetWithRequestParamsAndHeader")
    public JSONObject sendGetWithRequestParamsAndHeader(@RequestHeader("accept_token") String accept_token,
                                                        @RequestBody  JSONObject params) 
    {
        JSONObject header = new JSONObject();
        header.put("accept_token", accept_token);
        String url = ConvertUtils.toString(params.get("url"));
        params.remove("url");
        LogService.getgI().info("url " + url );
        JSONObject response = httpService.sendGetWithRequestParamsAndHeader(url, params, header).block();
        return response;
    }


    @PostMapping("sendPost")
    public JSONObject sendPost(@RequestBody Map<String, String> params) {
        JSONObject response = httpService.sendGet(params.get("url")).block();
        return response;
    }

    @PostMapping("sendPostWithRequestBody")
    public JSONObject sendPostWithRequestBody(@RequestBody JSONObject params) {
        String url = ConvertUtils.toString(params.get("url"));
        params.remove("url");
        LogService.getgI().info("url " + url );
        JSONObject response = httpService.sendPostWithRequestBody(url, params).block();
        return response;
    }


    @PostMapping("sendPostWithRequestBodyAndHeader")
    public JSONObject sendPostWithRequestBodyAndHeader(@RequestHeader("accept_token") String accept_token ,@RequestBody JSONObject params) {
        JSONObject header = new JSONObject();

        header.put("accept_token", accept_token);
        String url = ConvertUtils.toString(params.get("url"));
        params.remove("url");
        LogService.getgI().info("url " + url );
        JSONObject response = httpService.sendPostWithRequestBodyAndHeader(url, params, header).block();
        return response;
    }

    @GetMapping("success")
    public JSONObject success() {

        return Response.success();
    }







}
