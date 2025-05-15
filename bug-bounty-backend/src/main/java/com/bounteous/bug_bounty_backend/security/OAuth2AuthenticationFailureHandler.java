package com.bounteous.bug_bounty_backend.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class OAuth2AuthenticationFailureHandler implements AuthenticationFailureHandler {
    
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, 
                                      HttpServletResponse response, 
                                      AuthenticationException exception) throws IOException {
        
        // Log the error for debugging
        System.err.println("OAuth2 authentication failed: " + exception.getMessage());
        
        // Redirect to frontend login page with error message
        String targetUrl = "http://localhost:4200/login?error=" + 
                          URLEncoder.encode("OAuth2 authentication failed: " + exception.getMessage(), 
                                          StandardCharsets.UTF_8);
        response.sendRedirect(targetUrl);
    }
}