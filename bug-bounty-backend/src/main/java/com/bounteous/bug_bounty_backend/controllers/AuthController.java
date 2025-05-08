package com.bounteous.bug_bounty_backend.controllers;

import com.bounteous.bug_bounty_backend.data.dto.requests.auth.RegisterRequest;
import com.bounteous.bug_bounty_backend.data.dto.requests.auth.LoginRequest;
import com.bounteous.bug_bounty_backend.data.dto.responses.auth.JwtResponse;
import com.bounteous.bug_bounty_backend.data.dto.responses.ApiResponse;
import com.bounteous.bug_bounty_backend.exceptions.UnauthorizedException;
import com.bounteous.bug_bounty_backend.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}