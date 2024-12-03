package com.hcmute.prse_be.security;

import com.hcmute.prse_be.filter.JwtFilter;
import com.hcmute.prse_be.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


import java.util.Arrays;
import java.util.List;

@Configuration
public class SecurityConfig {


    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public BCryptPasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


    @Bean
    public DaoAuthenticationProvider authenticationProvider(CustomUserDetailsService userService)
    {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Thêm nhiều origins
        configuration.setAllowedOrigins(Arrays.asList(
                Endpoints.FRONT_END_HOST,
                "https://api-merchant.payos.vn",    // PayOS API domain
                "https://pay.payos.vn"              // PayOS payment domain
        ));

        // Hoặc cho phép tất cả origins (không recommended cho production)
        // configuration.setAllowedOrigins(Arrays.asList("*"));

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList(
                "Origin",
                "Content-Type",
                "Accept",
                "Authorization",
                "X-Requested-With",
                "x-client-id",        // PayOS headers
                "x-api-key",          // PayOS headers
                "Charset"
        ));
        configuration.setExposedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(config -> {
                    config
                            .requestMatchers(HttpMethod.GET, Endpoints.PUBLIC_GET_END_POINT).permitAll()
                            .requestMatchers("/ws/**").permitAll() // Cho phép truy cập WebSocket
                            .requestMatchers(HttpMethod.POST, Endpoints.PUBLIC_POST_END_POINT).permitAll()
                            .requestMatchers(HttpMethod.GET, Endpoints.STUDENT_GET_END_POINT).hasAuthority("STUDENT")
                            .requestMatchers(HttpMethod.POST, Endpoints.STUDENT_POST_END_POINT).hasAuthority("STUDENT")
                            .requestMatchers(HttpMethod.POST, Endpoints.ADMIN_POST_END_POINT).hasAuthority("ADMIN")
                            .requestMatchers(HttpMethod.GET, Endpoints.INSTRUCTOR_GET_END_POINT).hasAuthority("INSTRUCTOR")
                            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Thêm cho CORS preflight
                            .anyRequest().authenticated(); // Thêm default rule
                })
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Sử dụng CorsConfigurationSource
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .httpBasic(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable);
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


}
