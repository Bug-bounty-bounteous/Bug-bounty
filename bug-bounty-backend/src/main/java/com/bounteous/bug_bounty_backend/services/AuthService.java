package com.bounteous.bug_bounty_backend.services;

import com.bounteous.bug_bounty_backend.data.dto.requests.auth.CaptchaUnlockRequest;
import com.bounteous.bug_bounty_backend.data.dto.requests.auth.LoginRequest;
import com.bounteous.bug_bounty_backend.data.dto.requests.auth.RegisterRequest;
import com.bounteous.bug_bounty_backend.data.dto.responses.auth.JwtResponse;
import com.bounteous.bug_bounty_backend.data.entities.humans.Company;
import com.bounteous.bug_bounty_backend.data.entities.humans.Developer;
import com.bounteous.bug_bounty_backend.data.entities.humans.User;
import com.bounteous.bug_bounty_backend.data.repositories.humans.CompanyRepository;
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

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final DeveloperRepository developerRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    private static final int MAX_FAILED_ATTEMPTS = 3;

    @Transactional
    public JwtResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("This email is already registered. Please login.");
        }

        LocalDateTime now = LocalDateTime.now();

        if ("COMPANY".equalsIgnoreCase(request.getRole())) {
            Company company = Company.builder()
                    .companyName(request.getUsername())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role("COMPANY")
                    .accountLocked(false)
                    .failedAttempts(0)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();

            userRepository.save(company);

            String jwt = jwtTokenProvider.generateToken(company.getEmail());

            return JwtResponse.builder()
                    .token(jwt)
                    .type("Bearer")
                    .id(company.getId())
                    .email(company.getEmail())
                    .username(company.getCompanyName())
                    .role(company.getRole())
                    .build();
        }

        // Developer-specific validations
        if (developerRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already taken");
        }

        if (request.getFirstName() == null || request.getFirstName().trim().length() < 2) {
            throw new BadRequestException("First name is required for developer");
        }

        if (request.getLastName() == null || request.getLastName().trim().length() < 2) {
            throw new BadRequestException("Last name is required for developer");
        }

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
                .createdAt(now)
                .updatedAt(now)
                .build();

        userRepository.save(developer);

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
                .orElseThrow(() -> new UnauthorizedException("Invalid email"));

        // Account locked? Immediately return CAPTCHA prompt
        if (user.isAccountLocked()) {
            throw new UnauthorizedException("Account locked. Please enter CAPTCHA to unlock.");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.getPassword())
            );

            if (user.getFailedAttempts() > 0) {
                resetFailedAttempts(user);
            }

        } catch (AuthenticationException e) {
            incrementFailedAttempts(user);
            int attemptsLeft = MAX_FAILED_ATTEMPTS - user.getFailedAttempts();

            String message = (attemptsLeft > 0)
                    ? "Wrong password. " + attemptsLeft + " attempt(s) left."
                    : "Account locked. Please enter CAPTCHA to unlock.";

            throw new UnauthorizedException(message);
        }

        String jwt = request.isRememberMe()
                ? jwtTokenProvider.generateLongLivedToken(user.getEmail())
                : jwtTokenProvider.generateToken(user.getEmail());

        JwtResponse.JwtResponseBuilder responseBuilder = JwtResponse.builder()
                .token(jwt)
                .type("Bearer")
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole());

        if ("DEVELOPER".equalsIgnoreCase(user.getRole())) {
            Developer developer = developerRepository.findById(user.getId())
                    .orElseThrow(() -> new RuntimeException("Developer not found"));
            responseBuilder.username(developer.getUsername());

        } else if ("COMPANY".equalsIgnoreCase(user.getRole())) {
            Company company = companyRepository.findById(user.getId())
                    .orElseThrow(() -> new RuntimeException("Company not found"));
            responseBuilder.username(company.getCompanyName());
        }

        return responseBuilder.build();
    }

    public JwtResponse refreshToken(String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new UnauthorizedException("Invalid token");
        }

        String email = jwtTokenProvider.getEmailFromToken(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        String newToken = jwtTokenProvider.generateToken(email);

        JwtResponse.JwtResponseBuilder responseBuilder = JwtResponse.builder()
                .token(newToken)
                .type("Bearer")
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole());

        if ("DEVELOPER".equalsIgnoreCase(user.getRole())) {
            Developer developer = developerRepository.findById(user.getId())
                    .orElseThrow(() -> new RuntimeException("Developer not found"));
            responseBuilder.username(developer.getUsername());

        } else if ("COMPANY".equalsIgnoreCase(user.getRole())) {
            Company company = companyRepository.findById(user.getId())
                    .orElseThrow(() -> new RuntimeException("Company not found"));
            responseBuilder.username(company.getCompanyName());
        }

        return responseBuilder.build();
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

    public String unlockAccount(CaptchaUnlockRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isAccountLocked()) {
            return "Account is not locked.";
        }

        if (!"ABC123".equals(request.getCaptchaCode())) {
            throw new RuntimeException("Invalid CAPTCHA code.");
        }

        user.setAccountLocked(false);
        user.setFailedAttempts(0);
        userRepository.save(user);

        return "Account unlocked successfully.";
    }
}
