package com.bounteous.bug_bounty_backend.controllers;

import com.bounteous.bug_bounty_backend.data.dto.requests.auth.CaptchaRequest;
import com.bounteous.bug_bounty_backend.data.dto.requests.auth.CaptchaUnlockRequest;
import com.bounteous.bug_bounty_backend.data.dto.requests.auth.RegisterRequest;
import com.bounteous.bug_bounty_backend.data.dto.requests.auth.LoginRequest;
import com.bounteous.bug_bounty_backend.data.dto.responses.auth.JwtResponse;
import com.bounteous.bug_bounty_backend.data.dto.responses.ApiResponse;
import com.bounteous.bug_bounty_backend.exceptions.UnauthorizedException;
import com.bounteous.bug_bounty_backend.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<JwtResponse> register(
            @RequestBody @Valid RegisterRequest request
    ) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(
            @RequestBody @Valid LoginRequest request
    ) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/refresh-token")
    public ResponseEntity<JwtResponse> refreshToken(@RequestHeader("Authorization") String authHeader) {
        // Extract token from Authorization header
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            JwtResponse response = authService.refreshToken(token);
            return ResponseEntity.ok(response);
        }
        throw new UnauthorizedException("Invalid token");
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout() {
        // We'll implement a simple logout response as JWT is stateless
        return ResponseEntity.ok(new ApiResponse(true, "Logout successful"));
    }

    @PostMapping("/unlock")
    public ResponseEntity<ApiResponse> unlockAccount(@RequestBody CaptchaUnlockRequest request) {
        String message = authService.unlockAccount(request);
        return ResponseEntity.ok(new ApiResponse(true, message));
    }

    @PostMapping("/verify-captcha")
    public ResponseEntity<Map<String, String>> verifyCaptcha(@RequestBody CaptchaRequest request) {
        String url = "https://www.google.com/recaptcha/api/siteverify";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("secret", "6LeZ6TsrAAAAABhB2Fsf5lP3Dtn7GaBQQg-M44aq");
        params.add("response", request.getCaptcha());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

        // System.out.println("reCAPTCHA response: " + response.getBody()); 
        boolean success = Boolean.TRUE.equals(response.getBody().get("success"));
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Verified"));
        } else {
            return ResponseEntity.status(403).body(Map.of("message", "CAPTCHA Failed"));
        }
    }

}
