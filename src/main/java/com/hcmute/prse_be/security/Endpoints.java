package com.hcmute.prse_be.security;

import com.hcmute.prse_be.config.Config;

public class Endpoints {

    public static final String FRONT_END_HOST = Config.getParam("spring_boot", "front_end_host_test");

    public static final String[] PUBLIC_GET_END_POINT = {
            "/api/home/**",


//            "/books",
//            "/books/**",
//            "/images",
//            "/images/**",
//            "/users/search/existsByUsername",
//            "/users/search/existsByUserEmail",
//            "/user/activate"
    };

    public static final String[] PUBLIC_POST_END_POINT = {
//            "/user/register",
//            "/user/login",
//            "/test/**"
    };

    public static final String[] ADMIN_GET_END_POINT = {
//            "/users",
//            "/users/**"
    };

    public static final String[] ADMIN_POST_END_POINT = {
//            "/books",
    };
}
