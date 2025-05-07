package com.bounteous.bug_bounty_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

// JWT configuration properties
@Configuration
public class JwtConfig {
    @Value("${jwt.secret:defaultSecret}")
    private String secret;
    
    @Value("${jwt.expiration:86400000}")
    private long expiration; // 24 hours default
    
    public String getSecret() {
        return secret;
    }
    
    public long getExpiration() {
        return expiration;
    }
}
