package com.hcmute.prse_be.rest;

import com.hcmute.prse_be.constants.ApiPaths;
import com.hcmute.prse_be.constants.ErrorMsg;
import com.hcmute.prse_be.entity.SubCategoryEntity;
import com.hcmute.prse_be.response.Response;
import com.hcmute.prse_be.service.CategoryService;
import com.hcmute.prse_be.service.LogService;
import com.hcmute.prse_be.util.ConvertUtils;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPaths.CATEGORY_API)
public class CategoryAPI {

    private final CategoryService categoryService;

    @Autowired
    public CategoryAPI(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping(ApiPaths.CATEGORY_PATH_ID)
    public JSONObject getCourseBySubCategoryId(@PathVariable Long id,
                                               @RequestParam(defaultValue = "0") Integer page,
                                               Authentication authentication
    ) {
        LogService.getgI().info("[CategoryAPI] getCourseBySubCategoryId : " + id + " page: "+page);
        JSONObject response = new JSONObject();
        try {
            SubCategoryEntity subCategory = categoryService.getSubCategoryById(ConvertUtils.toLong(id));


            // check subcategory exist or not
            if(subCategory == null || !subCategory.getIsActive())
            {
                return Response.error(ErrorMsg.INVALID_SUB_CATEGORY);
            }

            response.put("total_courses", categoryService.getCoursesBySubCategory(ConvertUtils.toLong(id), page, authentication));
        } catch (Exception e) {
            return Response.error("Failed");
        }
        return Response.success(response);
    }

    @GetMapping(ApiPaths.CATEGORY_PATH_ID_FILTERS)
    public JSONObject getCoursesBySubCategoryWithFilters(
            @PathVariable Long id,
            @RequestParam(defaultValue = "") String q,  // Thêm keyword search
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "all") String price,
            @RequestParam(defaultValue = "0") Integer rating,
            @RequestParam(defaultValue = "newest") String sort,
            Authentication authentication
    ) {
        LogService.getgI().info("[CategoryAPI] getCoursesBySubCategoryWithFilters: " +
                "id=" + id + ", q=" + q + ", price=" + price +
                ", rating=" + rating + ", sort=" + sort);

        JSONObject response = new JSONObject();
        try {
            SubCategoryEntity subCategory = categoryService.getSubCategoryById(id);

            if(subCategory == null || !subCategory.getIsActive()) {
                return Response.error(ErrorMsg.INVALID_SUB_CATEGORY);
            }

            response.put("total_courses", categoryService.getCoursesBySubCategoryWithFilters(
                    id, q, page, price, rating, sort, authentication
            ));
        } catch (Exception e) {
            return Response.error("Failed");
        }
        return Response.success(response);
    }
}
