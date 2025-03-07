package com.hcmute.prse_be.rest;

import com.hcmute.prse_be.constants.ApiPaths;
import com.hcmute.prse_be.constants.ErrorMsg;
import com.hcmute.prse_be.response.Response;
import com.hcmute.prse_be.service.CourseService;
import com.hcmute.prse_be.service.LogService;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPaths.SEARCH_API)
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
            return Response.error(ErrorMsg.SOMETHING_WENT_WRONG);
        }
        return Response.success(response);
    }

    @GetMapping(ApiPaths.SEARCH_FILERS)
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
            return Response.error(ErrorMsg.SOMETHING_WENT_WRONG);
        }
        return Response.success(response);
    }



}
