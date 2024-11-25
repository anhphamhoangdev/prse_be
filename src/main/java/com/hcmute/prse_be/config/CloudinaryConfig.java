package com.hcmute.prse_be.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    private final String cloudName = Config.getParam("cloudinary", "cloud_name");

    private final String apiKey = Config.getParam("cloudinary", "api_key");

    private final String apiSecret = Config.getParam("cloudinary", "api_secret");

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }
}
