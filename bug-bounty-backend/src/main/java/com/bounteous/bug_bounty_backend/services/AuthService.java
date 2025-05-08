package com.bounteous.bug_bounty_backend.services;

import com.bounteous.bug_bounty_backend.data.dto.requests.auth.LoginRequest;
import com.bounteous.bug_bounty_backend.data.dto.requests.auth.RegisterRequest;
import com.bounteous.bug_bounty_backend.data.dto.responses.auth.JwtResponse;
import com.bounteous.bug_bounty_backend.data.entities.humans.Developer;
import com.bounteous.bug_bounty_backend.data.entities.humans.User;
import com.bounteous.bug_bounty_backend.data.repositories.humans.DeveloperRepository;
import com.bounteous.bug_bounty_backend.data.repositories.humans.UserRepository;
import com.bounteous.bug_bounty_backend.exceptions.BadRequestException;
import com.bounteous.bug_bounty_backend.exceptions.UnauthorizedException;
import com.bounteous.bug_bounty_backend.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final DeveloperRepository developerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCK_TIME_MINUTES = 15;
    
    @Transactional
public JwtResponse register(RegisterRequest request) {
    // Existing code remains unchanged
    if (userRepository.existsByEmail(request.getEmail())) {
        throw new BadRequestException("Email already in use");
    }
    
    if (developerRepository.existsByUsername(request.getUsername())) {
        throw new BadRequestException("Username already taken");
    }
    
    LocalDateTime now = LocalDateTime.now();
    
    Developer developer = Developer.builder()
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .role("DEVELOPER")
            .username(request.getUsername())
            .rating(0.0f)
            .points(0)
            .accountLocked(false)
            .failedAttempts(0)
            .createdAt(now)  // Set created_at explicitly
            .updatedAt(now)  // Also set updated_at for consistency
            .build();
    
    developerRepository.save(developer);
    
    String jwt = jwtTokenProvider.generateToken(developer.getEmail());
    
    return JwtResponse.builder()
            .token(jwt)
            .type("Bearer")
            .id(developer.getId())
            .email(developer.getEmail())
            .username(developer.getUsername())
            .role(developer.getRole())
            .build();
}
    
    @Transactional
    public JwtResponse login(LoginRequest request) {
        String email = request.getEmail();
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));
                
        // Check if account is locked
        if (user.isAccountLocked()) {
            // Check if lock time has expired
            Long lockTimeValue = user.getLockTime();
            if (lockTimeValue != null) {
                long currentTimeMillis = System.currentTimeMillis();
                if (currentTimeMillis < lockTimeValue) {
                    throw new UnauthorizedException("Account is temporarily locked due to multiple failed login attempts. Please try again later.");
                } else {
                    // Lock time expired, unlock account
                    unlockAccount(user);
                }
            }
        }
        
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            email,
                            request.getPassword()
                    )
            );
            
            // Reset failed attempts on successful login
            if (user.getFailedAttempts() > 0) {
                resetFailedAttempts(user);
            }
            
        } catch (AuthenticationException e) {
            // Increment failed attempts
            incrementFailedAttempts(user);
            throw new UnauthorizedException("Invalid email or password");
        }
        
        // Generate token with longer expiration if "Remember me" is selected
        String jwt = request.isRememberMe() ? 
                jwtTokenProvider.generateLongLivedToken(user.getEmail()) :
                jwtTokenProvider.generateToken(user.getEmail());
        
        JwtResponse response = JwtResponse.builder()
                .token(jwt)
                .type("Bearer")
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
                
        if (user instanceof Developer developer) {
            response.setUsername(developer.getUsername());
        }
        
        return response;
    }
    
    public JwtResponse refreshToken(String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new UnauthorizedException("Invalid token");
        }
        
        String email = jwtTokenProvider.getEmailFromToken(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
        
        String newToken = jwtTokenProvider.generateToken(email);
        
        JwtResponse response = JwtResponse.builder()
                .token(newToken)
                .type("Bearer")
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
                
        if (user instanceof Developer developer) {
            response.setUsername(developer.getUsername());
        }
        
        return response;
    }
    
    @Transactional
    public void incrementFailedAttempts(User user) {
        int newFailAttempts = user.getFailedAttempts() + 1;
        user.setFailedAttempts(newFailAttempts);
        
        if (newFailAttempts >= MAX_FAILED_ATTEMPTS) {
            lockAccount(user);
        }
        
        userRepository.save(user);
    }
    
    @Transactional
    public void lockAccount(User user) {
        user.setAccountLocked(true);
        // Set lock time to current time + lock duration
        user.setLockTime(System.currentTimeMillis() + (LOCK_TIME_MINUTES * 60 * 1000));
        userRepository.save(user);
    }
    
    @Transactional
    public void unlockAccount(User user) {
        user.setAccountLocked(false);
        user.setLockTime(null);
        user.setFailedAttempts(0);
        userRepository.save(user);
    }
    
    @Transactional
    public void resetFailedAttempts(User user) {
        user.setFailedAttempts(0);
        userRepository.save(user);
    }
}