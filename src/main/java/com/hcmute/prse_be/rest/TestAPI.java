package com.hcmute.prse_be.rest;

import com.hcmute.prse_be.response.Response;
import net.minidev.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestAPI {

    @GetMapping
    public JSONObject test() {
        return Response.success();
    }


}
