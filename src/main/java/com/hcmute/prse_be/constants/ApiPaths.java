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

    // ADMIN API
    public static final String ADMIN_API = "/api/admin";

    // CART API
    public static final String CART_API = "/api/cart";
    public static final String CART_REMOVE_ITEM_ID = "/remove-course/{itemId}";
    public static final String CART_COUNT_ITEM = "/count";
    public static final String CART_GET_CART = "/get-cart";
    public static final String CART_ADD_ITEM = "/add-to-cart";

    // CHECKOUT API
    public static final String CHECKOUT_API = "/api/checkout";
    public static final String CHECKOUT_CREATE = "/create";

    // CLOUDINARY API
    public static final String CLOUDINARY_API = "/api/upload";
    public static final String CLOUDINARY_CHECK_STATUS_THREAD_ID = "/status/{threadId}";
    public static final String CLOUDINARY_GET_ALL_STATUS= "/getAllStatuses";
    public static final String CLOUDINARY_CHECK_STATUS_OF_INSTRUCTOR_ID= "/status/instructor/{instructorId}";

    // COURSE API
    public static final String COURSE_API = "api/course";
    public static final String COURSE_PATH_ID="{id}";
    public static final String COURSE_GET_FEEDBACK_ID ="/{id}/feedbacks";
    public static final String COURSE_CURRICULUM_ID = "{id}/curriculum";
    public static final String COURSE_GET_LESSON = "/{courseId}/{chapterId}/{lessonId}/video";
    public static final String COURSE_SUBMIT_VIDEO ="/video/submit";
    public static final String COURSE_GET_LIST_COURSE_STUDENT = "/my-courses";

    // INSTRUCTOR API
    public static final String INSTRUCTOR_API = "/api/instructor";

}
