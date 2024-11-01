package com.hcmute.prse_be.security;

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
//import vn.harris.bookstore_be.filter.JwtFilter;
//import vn.harris.bookstore_be.service.UserService;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SecurityConfig {


//    @Autowired
//    private JwtFilter jwtFilter;

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


//    @Bean
//    public DaoAuthenticationProvider authenticationProvider(UserService userService)
//    {
//        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
//        daoAuthenticationProvider.setUserDetailsService(userService);
//        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
//        return daoAuthenticationProvider;
//    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(Endpoints.FRONT_END_HOST));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Thêm OPTIONS
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("*")); // Thêm exposed headers
        configuration.setAllowCredentials(true);  // Thêm allow credentials
        configuration.setMaxAge(3600L);  // Thêm max age

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
                            .requestMatchers(HttpMethod.POST, Endpoints.PUBLIC_POST_END_POINT).permitAll()
                            .requestMatchers(HttpMethod.GET, Endpoints.ADMIN_GET_END_POINT).hasAuthority("ADMIN")
                            .requestMatchers(HttpMethod.POST, Endpoints.ADMIN_POST_END_POINT).hasAuthority("ADMIN")
                            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Thêm cho CORS preflight
                            .anyRequest().authenticated(); // Thêm default rule
                })
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Sử dụng CorsConfigurationSource
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .httpBasic(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable);
//        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


}