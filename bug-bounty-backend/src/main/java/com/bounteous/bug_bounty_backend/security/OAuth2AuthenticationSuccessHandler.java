package com.bounteous.bug_bounty_backend.security;

import com.bounteous.bug_bounty_backend.services.AuthService;
import com.bounteous.bug_bounty_backend.data.entities.humans.User;
import com.bounteous.bug_bounty_backend.data.entities.humans.Developer;
import com.bounteous.bug_bounty_backend.data.entities.humans.Company;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    
    private final JwtTokenProvider tokenProvider;
    private final AuthService authService;
    
    public OAuth2AuthenticationSuccessHandler(
            JwtTokenProvider tokenProvider, 
            @Lazy AuthService authService) {
        this.tokenProvider = tokenProvider;
        this.authService = authService;
    }
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                      HttpServletResponse response, 
                                      Authentication authentication) throws IOException {
        
        OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
        OAuth2User oauth2User = oauth2Token.getPrincipal();
        
        // Extract user information from OAuth2 provider
        Map<String, Object> attributes = oauth2User.getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        
        // Split name into first and last name
        String[] nameParts = name != null ? name.split(" ", 2) : new String[]{"", ""};
        String firstName = nameParts.length > 0 ? nameParts[0] : "";
        String lastName = nameParts.length > 1 ? nameParts[1] : "";
        
        try {
            // Create or retrieve user from database
            User user = authService.findOrCreateOAuth2User(
                email, 
                firstName,
                lastName,
                oauth2Token.getAuthorizedClientRegistrationId(),
                (String) attributes.get("sub") // Google user ID
            );
            
            // Generate JWT token with user details
            String jwtToken = tokenProvider.generateToken(user.getEmail());
            
            // Build user info for frontend
            StringBuilder userInfo = new StringBuilder();
            userInfo.append("&id=").append(user.getId());
            userInfo.append("&role=").append(user.getRole());
            userInfo.append("&email=").append(URLEncoder.encode(user.getEmail(), StandardCharsets.UTF_8));
            
            if (user instanceof Developer) {
                Developer dev = (Developer) user;
                userInfo.append("&username=").append(URLEncoder.encode(dev.getUsername(), StandardCharsets.UTF_8));
            } else if (user instanceof Company) {
                Company comp = (Company) user;
                userInfo.append("&username=").append(URLEncoder.encode(comp.getCompanyName(), StandardCharsets.UTF_8));
            }
            
            // Redirect to frontend with token and user info
            String targetUrl = "http://localhost:4200/oauth2/callback?token=" + 
                              URLEncoder.encode(jwtToken, StandardCharsets.UTF_8) + userInfo.toString();
            response.sendRedirect(targetUrl);
            
        } catch (Exception e) {
            System.err.println("OAuth2 Success Handler Error: " + e.getMessage());
            e.printStackTrace();
            
            // Handle error by redirecting to login with error message
            String errorUrl = "http://localhost:4200/login?error=" + 
                             URLEncoder.encode("OAuth2 authentication failed", StandardCharsets.UTF_8);
            response.sendRedirect(errorUrl);
        }
    }
}