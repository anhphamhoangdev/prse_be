package com.hcmute.prse_be.constants;

public class ApiPaths {

    // HOME API
    public static final String HOME_API = "/api/home";
    public static final String HOME_BANNERS = "/banners";
    public static final String HOME_CATEGORY = "/categories";
    public static final String HOME_FREE_COURSE = "/free-courses";
    public static final String HOME_HOT_COURSE = "/hot-courses";
    public static final String HOME_DISCOUNT_COURSE = "/discount-courses";


    // CATEGORY API
    public static final String CATEGORY_API = "/api/category";
    public static final String CATEGORY_PATH_ID = "/{id}";
    public static final String CATEGORY_PATH_ID_FILTERS = "/{id}/filters";


    // SEARCH API
    public static final String SEARCH_API = "/api/search";
    public static final String SEARCH_FILERS = "/filters";


    // STUDENT API
    public static final String STUDENT_API = "/api/student";
    public static final String CHECK_EXIST_USERNAME = "/existsByUsername";
    public static final String CHECK_EXIST_EMAIL = "/existsByEmail";
    public static final String CHECK_EXIST_PHONE_NUMBER = "/existsByPhoneNumber";
    public static final String ACTIVATE_ACCOUNT = "/activate";
    public static final String REGISTER_ACCOUNT = "/register";
    public static final String LOGIN = "/login";
    public static final String GET_PROFILE = "/profile";
    public static final String UPDATE_AVATAR = "/update-avatar";
}
