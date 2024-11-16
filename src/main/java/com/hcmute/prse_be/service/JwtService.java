package com.hcmute.prse_be.service;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

public interface JwtService {
    String generateToken(UserDetails userDetails, boolean rememberMe);
    String extractUsername(String token);
    Date extractExpiration(String token);
    Boolean validateToken(String token);
    Boolean validateToken(String token, UserDetails userDetails);
}
