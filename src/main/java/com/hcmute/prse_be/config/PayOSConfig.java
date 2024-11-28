package com.hcmute.prse_be.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.payos.PayOS;

@Configuration
public class PayOSConfig {

    private final String clientId = Config.getParam("payos", "client_id");

    private final String apiKey = Config.getParam("payos", "api_key");

    private final String checksumKey = Config.getParam("payos", "checksum_key");

    @Bean
    public PayOS payOS() {
        return new PayOS(clientId, apiKey, checksumKey);
    }

}
