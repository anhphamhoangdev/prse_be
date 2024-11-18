package com.hcmute.prse_be.rest;

import com.hcmute.prse_be.repository.CourseRepository;
import com.hcmute.prse_be.response.Response;
import com.hcmute.prse_be.service.CourseService;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestAPI {

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    CourseService courseService;


    @GetMapping("{id}")
    public JSONObject test(@PathVariable("id") Long id, Authentication authentication) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("course", courseService.getDetailCourse(id, authentication));
        return Response.success(jsonObject);
    }


}
