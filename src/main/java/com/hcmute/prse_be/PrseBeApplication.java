package com.hcmute.prse_be;

import com.hcmute.prse_be.config.Config;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.util.Properties;

@SpringBootApplication
public class PrseBeApplication {

    public static final String SPRING_BOOT_CONFIG = Config.getParam("spring_boot", "conf_path");

    public static void main(String[] args) {
        new SpringApplicationBuilder(PrseBeApplication.class)
                .properties(getProperties())
                .run(args);
    }

    static Properties getProperties() {
        Properties props = new Properties();
        props.put("spring.config.location", SPRING_BOOT_CONFIG);
        return props;
    }
}
