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

    // CHAT API
    public static final String CHAT_API = "/api/chat";



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
    public static final String UPDATE_PASSWORD = "/update-password";

    // ADMIN API
    public static final String ADMIN_API = "/api/admin";
    public static final String ADMIN_OVERVIEW = "/overview";
    public static final String REVENUE = "/revenue";
    public static final String GET_CATEGORY_DISTRIBUTION = "/category-distribution";
    public static final String GET_STUDENTS = "/students";
    public static final String UPDATE_STUDENT_STATUS ="/students/{studentId}/toggle-status";
    public static final String UPDATE_INSTRUCTOR_STATUS = "/instructors/{instructorId}/toggle-status";
    public static final String GET_WITHDRAWS="/withdraws";
    public static final String UPDATE_WITHDRAW_STATUS = "/withdraws/{withdrawId}";

    // CART API
    public static final String CART_API = "/api/cart";
    public static final String CART_REMOVE_ITEM_ID = "/remove-course/{itemId}";
    public static final String CART_COUNT_ITEM = "/count";
    public static final String CART_GET_CART = "";
    public static final String CART_ADD_ITEM = "";

    // CHECKOUT API
    public static final String CHECKOUT_API = "/api/checkout";
    public static final String CHECKOUT_CREATE = "/create";


    // CLOUDINARY API
    public static final String CLOUDINARY_API = "/api/upload";
    public static final String CLOUDINARY_CHECK_STATUS_THREAD_ID = "/status/{threadId}";
    public static final String CLOUDINARY_GET_ALL_STATUS= "/getAllStatuses";
    public static final String CLOUDINARY_CHECK_STATUS_INSTRUCTOR_ID= "/status/instructor/{instructorId}";

    // COURSE API
    public static final String COURSE_API = "api/course";
    public static final String COURSE_PATH_ID="{id}";
    public static final String COURSE_GET_FEEDBACK_ID ="/{id}/feedbacks";
    public static final String COURSE_CURRICULUM_ID = "{id}/curriculum";
    public static final String COURSE_GET_VIDEO_LESSON = "/{courseId}/{chapterId}/{lessonId}/video";
    public static final String COURSE_SUBMIT_VIDEO ="/video/submit";
    public static final String COURSE_GET_LIST_COURSE_STUDENT = "/my-courses";
    public static final String COURSE_SUBMIT_FEEDBACK = "/feedback";
    public static final String COURSE_GET_ALL_FEEDBACK ="/{courseId}/all-feedbacks";



    // INSTRUCTOR API
    public static final String INSTRUCTOR_API = "/api/instructor";
    public static final String INSTRUCTOR_GET_COURSES = "/courses";
    public static final String INSTRUCTOR_GET_RECENT_ENROLL = "/recent-enrollments";
    public static final String INSTRUCTOR_UPLOAD_COURSE = "/upload-course";
    public static final String INSTRUCTOR_UPLOAD_PREVIEW_VIDEO ="/upload-preview-video";
    public static final String INSTRUCTOR_UPLOAD_STATUS="/upload-status";
    public static final String INSTRUCTOR_GET_COURSE_ID = "/courses/{courseId}";
    public static final String INSTRUCTOR_UPDATE_COURSE_ID ="/courses/{courseId}";
    public static final String INSTRUCTOR_GET_COURSE_CURRICULUM = "/courses/{courseId}/curriculum";
    public static final String INSTRUCTOR_GET_CHAPTER_ID ="/courses/{courseId}/chapter/{chapterId}";
    public static final String INSTRUCTOR_GET_LESSON_ID ="/courses/{courseId}/chapter/{chapterId}/lesson/{lessonId}";
    public static final String INSTRUCTOR_UPDATE_LESSON_ID ="/courses/{courseId}/chapter/{chapterId}/lesson/{lessonId}";
    public static final String INSTRUCTOR_GET_LESSON_DETAILS ="/courses/{courseId}/chapter/{chapterId}/lesson/{lessonId}/details";
    public static final String INSTRUCTOR_POST_LESSON_INFOR = "/courses/{courseId}/chapter/{chapterId}/lessons";
    public static final String INSTRUCTOR_POST_LESSON_DRAFT_INFOR = "/courses/{courseId}/chapter/{chapterId}/lesson-draft";
    public static final String INSTRUCTOR_UPLOAD_LESSON_VIDEO ="/courses/{courseId}/chapter/{chapterId}/lesson/{lessonId}/video/upload";
    public static final String INSTRUCTOR_UPLOAD_LESSON_VIDEO_DRAFT ="/courses/{courseId}/chapter/{chapterId}/lesson/{lessonDraftId}/video-draft/upload";

    public static final String INSTRUCTOR_CREATE_CHAPTER = "/courses/{courseId}/curriculum/chapters";
    public static final String INSTRUCTOR_CHECK_UPLOAD_STATUS ="/courses/{courseId}/chapter/{chapterId}/lesson/{lessonId}/video/upload-status";
    public static final String INSTRUCTOR_WITHDRAW_STUDENT ="/withdraw-student-account";
    public static final String INSTRUCTOR_WITHDRAW_BANK ="/withdraw-bank";
    public static final String INSTRUCTOR_UPDATE_CHAPTER ="/courses/{courseId}/chapter/{chapterId}";
    public static final String INSTRUCTOR_POPULAR_POSITION ="/common-titles";
    public static final String INSTRUCTOR_UPDATE_PROFILE = "/update-profile";

    //BANK API
    public static final String BANKS_API = "/api/banks";

    //PAYMENT API
    public static final String PAYMENT_API ="/api/payment";
    public static final String PAYMENT_CREATE = "/create";
    public static final String PAYMENT_CREATE_INSTRUCTOR ="/create-instructor";
    public static final String PAYMENT_UPDATE_STATUS ="/update-status";
    public static final String PAYMENT_UPDATE_STATUS_INSTRUCTOR ="/update-status-instructor";


    //PAYMENT METHOD API
    public static final String PAYMENT_METHOD_API ="/api/payment-method";

    // TEST API
    public static final String TEST_API ="/api/test";
    public static final String TEST_SEND_NOTIFY_INSTRUCTOR_ID = "/send-notification/{instructorId}";

    // QUIZ API
    public static final String QUIZ_API = "/api/quiz";
}
