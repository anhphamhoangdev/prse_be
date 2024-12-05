package com.hcmute.prse_be.security;

import com.hcmute.prse_be.config.Config;

public class Endpoints {

    public static final String FRONT_END_HOST = Config.getParam("spring_boot", "front_end_host");

    public static final String[] PUBLIC_GET_END_POINT = {
            "/api/home/**",

            "/api/category/**",

            "/api/search/**",

            "/api/student/activate",

            "/api/test",

            "/api/test/**",

            "/api/course/**",

            "/api/payment-method",



//            "/books",
//            "/books/**",
//            "/images",
//            "/images/**",
//            "/users/search/existsByUsername",
//            "/users/search/existsByUserEmail",
//            "/user/activate"
    };

    public static final String[] PUBLIC_POST_END_POINT = {
            "api/student/register",
            "api/student/existsByUsername",
            "api/student/existsByEmail",
            "api/student/existsByPhoneNumber",
            "api/student/login",
            "api/test/**",
            "api/payment/callbackUrl",
            "api/payment/confirm-webhook",
            "api/payment/update-status",
            "api/admin/login",

//            "/user/login",
//            "/test/**"
    };

    public static final String[] STUDENT_GET_END_POINT = {
            "/api/student/profile",
            "/api/cart/**",
            "/api/course/my-courses",

//            "/users",
//            "/users/**"
    };

    public static final String[] STUDENT_POST_END_POINT = {
            "/api/student/update-avatar",
            "/api/checkout/create",
            "/api/payment/create",
            "/api/chat"
//            "/users",
//            "/users/**"
    };

    public static final String[] ADMIN_GET_END_POINT = {
            "/api/admin/**",
    };

    public static final String[] ADMIN_POST_END_POINT = {
//            "/books",
            "/api/admin/**",
    };

    public static final String[] ADMIN_PUT_END_POINT = {
//            "/books",
            "/api/admin/**",
    };


    public static final String[] INSTRUCTOR_GET_END_POINT = {
//            "/books",
            "/api/instructor/**",
    };

}
