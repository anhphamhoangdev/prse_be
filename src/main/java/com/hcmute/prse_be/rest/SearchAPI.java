package com.hcmute.prse_be.rest;

import com.hcmute.prse_be.constants.ErrorMsg;
import com.hcmute.prse_be.entity.SubCategoryEntity;
import com.hcmute.prse_be.response.Response;
import com.hcmute.prse_be.service.CourseService;
import com.hcmute.prse_be.service.LogService;
import com.hcmute.prse_be.util.ConvertUtils;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
public class SearchAPI {

    private final CourseService courseService;

    @Autowired
    public SearchAPI(CourseService courseService) {
        this.courseService = courseService;
    }


    @GetMapping()
    public JSONObject searchCourseByKeywords(@RequestParam(defaultValue = "") String q, @RequestParam(defaultValue = "0") Integer page){
        LogService.getgI().info("[SearchAPI] searchCourseByKeywords : " + q);
        JSONObject response = new JSONObject();

        try {
            response.put("total_courses", courseService.getCoursesBySubCategory(q, page));
        } catch (Exception e) {
            return Response.error("Failed");
        }
        return Response.success(response);
    }

    @GetMapping("/filters")
    public JSONObject searchCourses(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "all") String price,
            @RequestParam(defaultValue = "0") Integer rating,
            @RequestParam(defaultValue = "newest") String sort
    ) {
        LogService.getgI().info("[SearchAPI] searchCourses with filters: " +
                "q=" + q + ", price=" + price + ", rating=" + rating +
                ", sort=" + sort);

        JSONObject response = new JSONObject();
        try {
            response.put("total_courses", courseService.searchCoursesWithFilters(
                    q, page, price, rating, sort
            ));
        } catch (Exception e) {
            return Response.error("Failed");
        }
        return Response.success(response);
    }




}
