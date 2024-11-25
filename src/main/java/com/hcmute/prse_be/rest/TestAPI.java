package com.hcmute.prse_be.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hcmute.prse_be.repository.CourseRepository;
import com.hcmute.prse_be.response.Response;
import com.hcmute.prse_be.service.CourseService;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vn.payos.PayOS;

import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestAPI {

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    CourseService courseService;
    @Autowired
    private PayOS payOS;


    @GetMapping("{id}")
    public JSONObject test(@PathVariable("id") Long id, Authentication authentication) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("course", courseService.getDetailCourse(id, authentication));
        return Response.success(jsonObject);
    }




}
