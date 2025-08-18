package com.project.habitflow.service;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Map;

public interface JwtService {
    String extractUsername(String token);
    boolean isTokenValid(String token, UserDetails userDetails);
    String generateToken(Map<String, Object> claims, UserDetails userDetails);

    List<String> extractRoles(String jwt);
}
