package com.example.catalog_service.config;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String extractUsername(String token);
    boolean isTokenValid(String token, UserDetails user);
}
