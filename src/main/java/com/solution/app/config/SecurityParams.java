package com.solution.app.config;

public interface SecurityParams {
    String HEADER_NAME = "Authorization";
    String SECRET = "jwtSecureAppSecretKey";
    String HEADER_PREFIX = "Bearer ";
}
