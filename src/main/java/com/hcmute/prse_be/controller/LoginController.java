package com.hcmute.prse_be.controller;

import com.hcmute.prse_be.config.Config;
import com.hcmute.prse_be.response.Response;
import com.hcmute.prse_be.service.HttpService;
import com.hcmute.prse_be.util.ConvertUtils;
import jakarta.servlet.http.HttpServletRequest;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    private static final String INFO_IP_URL = Config.getParam("info_ip", "base_url");

    private final HttpService httpService;

    @Autowired
    public LoginController(HttpService httpService) {
        this.httpService = httpService;
    }

    @GetMapping("/testLogin")
    public JSONObject loginTest(HttpServletRequest request) {
        JSONObject loginInfo = new JSONObject();
        String ip = request.getRemoteAddr();
        String user_agent = request.getHeader("User-Agent");
        String url = INFO_IP_URL + "/" + ip;
        JSONObject response_from_info_ip = httpService.sendGet(url).block();
        if(response_from_info_ip.get("status") != null
                && ConvertUtils.toString(response_from_info_ip.get("status")).equals("success"))
        {
            loginInfo.put("country", ConvertUtils.toString(response_from_info_ip.get("country")));
            loginInfo.put("city", ConvertUtils.toString(response_from_info_ip.get("city")));
            loginInfo.put("lat", ConvertUtils.toString(response_from_info_ip.get("lat")));
            loginInfo.put("lng", ConvertUtils.toString(response_from_info_ip.get("lon")));
        }
        loginInfo.put("remote addr", ip);
        loginInfo.put("user-agent", user_agent);
        return Response.success(loginInfo);
    }
}
