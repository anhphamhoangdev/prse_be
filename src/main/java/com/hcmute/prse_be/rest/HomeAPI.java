package com.hcmute.prse_be.rest;

import com.hcmute.prse_be.constants.ApiPaths;
import com.hcmute.prse_be.constants.PaginationNumber;
import com.hcmute.prse_be.response.Response;
import com.hcmute.prse_be.service.BannerService;
import com.hcmute.prse_be.service.CategoryService;
import com.hcmute.prse_be.service.CourseService;
import com.hcmute.prse_be.service.LogService;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.HOME_API)
public class HomeAPI {

    private final BannerService bannerService;
    private final CategoryService categoryService;
    private final CourseService courseService;

    @Autowired
    public HomeAPI(BannerService bannerService, CategoryService categoryService, CourseService courseService) {
        this.bannerService = bannerService;
        this.categoryService = categoryService;
        this.courseService = courseService;
    }

    @GetMapping(ApiPaths.HOME_BANNERS)
    public JSONObject getAllBanner() {
        LogService.getgI().info("[HOME] " + ApiPaths.HOME_BANNERS);
        JSONObject response = new JSONObject();
        try {
            response.put("banners", bannerService.getAllBannerActive());
        } catch (Exception e) {
            response.put("banners", new JSONArray());
        }
        return Response.success(response);
    }


    @GetMapping(ApiPaths.HOME_CATEGORY)
    public JSONObject getAllCategory()
    {
        LogService.getgI().info("[HOME] " + ApiPaths.HOME_CATEGORY);
        JSONObject response = new JSONObject();
        try {
            response.put("categories", categoryService.getAllCategoryWithSubsActive());
        } catch (Exception e) {
            response.put("categories", new JSONArray());
        }
        return Response.success(response);
    }

    @GetMapping(ApiPaths.HOME_FREE_COURSE)
    public JSONObject getFreeCourse() {
        LogService.getgI().info("[HOME] " + ApiPaths.HOME_FREE_COURSE);
        JSONObject response = new JSONObject();
        try {
            response.put("courses", courseService.getFreeCourse(0, PaginationNumber.HOME_COURSE_PER_PAGE));
        } catch (Exception e) {
            response.put("courses", new JSONArray());
        }
        return Response.success(response);

    }

    @GetMapping(ApiPaths.HOME_DISCOUNT_COURSE)
    public JSONObject getDiscountCourse() {
        LogService.getgI().info("[HOME] " + ApiPaths.HOME_DISCOUNT_COURSE);
        JSONObject response = new JSONObject();
        try {
            response.put("courses", courseService.getDiscountCourse(0, PaginationNumber.HOME_COURSE_PER_PAGE));
        } catch (Exception e) {
            response.put("courses", new JSONArray());
        }
        return Response.success(response);
    }






}
