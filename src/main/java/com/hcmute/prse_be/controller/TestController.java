package com.hcmute.prse_be.controller;

import com.hcmute.prse_be.response.Response;
import com.hcmute.prse_be.service.LogService;
import com.hcmute.prse_be.util.JsonUtils;
import net.minidev.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class TestController {

    @PostMapping("/test")
    public JSONObject doTest(@RequestBody Map<String, Object> request){
        LogService.getgI().info("[TestController] doTest : " + JsonUtils.Serialize(request));
        return Response.success();
    }

    @PostMapping("/test1")
    public JSONObject doTest1(@RequestBody Map<String, Object> request){
        LogService.getgI().info("[TestController] doTest1 : " + JsonUtils.Serialize(request));
        return Response.error("Test1");
    }
}
