package com.bounteous.bug_bounty_backend.services;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private DeveloperRepository developerRepository;
    
    @Mock
    private CompanyRepository companyRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    
    @Mock
    private AuthenticationManager authenticationManager;
    
    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private Developer developer;
    private Company company;
    private User user;

    @BeforeEach
    void setUp() {
        // Set up test data
        registerRequest = RegisterRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .username("johndoe")
                .password("password123")
                .role("DEVELOPER")
                .build();

        loginRequest = LoginRequest.builder()
                .email("john.doe@example.com")
                .password("password123")
                .rememberMe(false)
                .build();

        developer = Developer.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .username("johndoe")
                .password("encodedPassword")
                .role("DEVELOPER")
                .rating(0.0f)
                .points(0)
                .createdAt(LocalDateTime.now())
                .build();

        company = Company.builder()
                .id(2L)
                .email("company@example.com")
                .companyName("Example Corp")
                .password("encodedPassword")
                .role("COMPANY")
                .createdAt(LocalDateTime.now())
                .build();

        user = developer;
    }

    // Developer Registration Tests
    @Test
    void registerDeveloper_Success() {
        // Given
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(developerRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(Developer.class))).thenReturn(developer);
        when(jwtTokenProvider.generateToken(developer.getEmail())).thenReturn("jwt-token");

        // When
        JwtResponse response = authService.register(registerRequest);

        // Then
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("Bearer", response.getType());
        assertEquals(developer.getId(), response.getId());
        assertEquals(developer.getEmail(), response.getEmail());
        assertEquals(developer.getUsername(), response.getUsername());
        assertEquals(developer.getRole(), response.getRole());

        verify(userRepository).existsByEmail(registerRequest.getEmail());
        verify(developerRepository).existsByUsername(registerRequest.getUsername());
        verify(passwordEncoder).encode(registerRequest.getPassword());
        verify(userRepository).save(any(Developer.class));
        verify(jwtTokenProvider).generateToken(developer.getEmail());
    }

    @Test
    void registerDeveloper_EmailAlreadyExists_ThrowsException() {
        // Given
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class, 
            () -> authService.register(registerRequest));
        
        assertEquals("This email is already registered. Please login.", exception.getMessage());
        verify(userRepository).existsByEmail(registerRequest.getEmail());
        verify(developerRepository, never()).existsByUsername(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerDeveloper_UsernameAlreadyTaken_ThrowsException() {
        // Given
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(developerRepository.existsByUsername(registerRequest.getUsername())).thenReturn(true);

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class, 
            () -> authService.register(registerRequest));
        
        assertEquals("Username already taken", exception.getMessage());
        verify(userRepository).existsByEmail(registerRequest.getEmail());
        verify(developerRepository).existsByUsername(registerRequest.getUsername());
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerDeveloper_MissingFirstName_ThrowsException() {
        // Given
        registerRequest.setFirstName(null);
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(developerRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class, 
            () -> authService.register(registerRequest));
        
        assertEquals("First name is required for developer", exception.getMessage());
    }

    @Test
    void registerDeveloper_MissingLastName_ThrowsException() {
        // Given
        registerRequest.setLastName(null);
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(developerRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class, 
            () -> authService.register(registerRequest));
        
        assertEquals("Last name is required for developer", exception.getMessage());
    }

    // Company Registration Tests
    @Test
    void registerCompany_Success() {
        // Given
        RegisterRequest companyRequest = RegisterRequest.builder()
                .username("Example Corp")
                .email("company@example.com")
                .password("password123")
                .role("COMPANY")
                .build();

        when(userRepository.existsByEmail(companyRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(companyRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(Company.class))).thenReturn(company);
        when(jwtTokenProvider.generateToken(company.getEmail())).thenReturn("jwt-token");

        // When
        JwtResponse response = authService.register(companyRequest);

        // Then
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("Bearer", response.getType());
        assertEquals(company.getId(), response.getId());
        assertEquals(company.getEmail(), response.getEmail());
        assertEquals(company.getCompanyName(), response.getUsername());
        assertEquals(company.getRole(), response.getRole());

        verify(userRepository).save(any(Company.class));
    }

    // Login Tests
    @Test
    void login_Success() {
        // Given
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(jwtTokenProvider.generateToken(user.getEmail())).thenReturn("jwt-token");
        when(developerRepository.findById(user.getId())).thenReturn(Optional.of(developer));

        // When
        JwtResponse response = authService.login(loginRequest);

        // Then
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals(user.getId(), response.getId());
        assertEquals(user.getEmail(), response.getEmail());
        assertEquals(developer.getUsername(), response.getUsername());
        assertEquals(user.getRole(), response.getRole());

        verify(userRepository).findByEmail(loginRequest.getEmail());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenProvider).generateToken(user.getEmail());
    }

    @Test
    void login_InvalidEmail_ThrowsException() {
        // Given
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());

        // When & Then
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, 
            () -> authService.login(loginRequest));
        
        assertEquals("Invalid email", exception.getMessage());
        verify(userRepository).findByEmail(loginRequest.getEmail());
        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    void login_InvalidPassword_ThrowsException() {
        // Given
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid password"));

        // When & Then
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, 
            () -> authService.login(loginRequest));
        
        assertTrue(exception.getMessage().contains("Wrong password"));
        verify(authService).incrementFailedAttempts(user);
    }

    @Test
    void login_AccountLocked_ThrowsException() {
        // Given
        user.setAccountLocked(true);
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));

        // When & Then
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, 
            () -> authService.login(loginRequest));
        
        assertEquals("Account locked. Please enter CAPTCHA to unlock.", exception.getMessage());
        verify(userRepository).findByEmail(loginRequest.getEmail());
        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    void login_RememberMe_GeneratesLongLivedToken() {
        // Given
        loginRequest.setRememberMe(true);
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(jwtTokenProvider.generateLongLivedToken(user.getEmail())).thenReturn("long-lived-token");
        when(developerRepository.findById(user.getId())).thenReturn(Optional.of(developer));

        // When
        JwtResponse response = authService.login(loginRequest);

        // Then
        assertNotNull(response);
        assertEquals("long-lived-token", response.getToken());
        verify(jwtTokenProvider).generateLongLivedToken(user.getEmail());
        verify(jwtTokenProvider, never()).generateToken(any());
    }

    // Account Locking Tests
    @Test
    void incrementFailedAttempts_LockAccountAfterMaxAttempts() {
        // Given
        user.setFailedAttempts(2); // MAX_FAILED_ATTEMPTS - 1
        when(userRepository.save(user)).thenReturn(user);

        // When
        authService.incrementFailedAttempts(user);

        // Then
        assertEquals(3, user.getFailedAttempts());
        assertTrue(user.isAccountLocked());
        verify(userRepository).save(user);
    }

    @Test
    void resetFailedAttempts_Success() {
        // Given
        user.setFailedAttempts(2);
        when(userRepository.save(user)).thenReturn(user);

        // When
        authService.resetFailedAttempts(user);

        // Then
        assertEquals(0, user.getFailedAttempts());
        verify(userRepository).save(user);
    }

    @Test
    void unlockAccount_Success() {
        // Given
        user.setAccountLocked(true);
        user.setFailedAttempts(3);
        when(userRepository.save(user)).thenReturn(user);

        // When
        authService.unlockAccount(user);

        // Then
        assertFalse(user.isAccountLocked());
        assertNull(user.getLockTime());
        assertEquals(0, user.getFailedAttempts());
        verify(userRepository).save(user);
    }

    @Test
    void refreshToken_Success() {
        // Given
        String token = "valid-token";
        when(jwtTokenProvider.validateToken(token)).thenReturn(true);
        when(jwtTokenProvider.getEmailFromToken(token)).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(jwtTokenProvider.generateToken(user.getEmail())).thenReturn("new-token");
        when(developerRepository.findById(user.getId())).thenReturn(Optional.of(developer));

        // When
        JwtResponse response = authService.refreshToken(token);

        // Then
        assertNotNull(response);
        assertEquals("new-token", response.getToken());
        assertEquals(user.getId(), response.getId());
        assertEquals(user.getEmail(), response.getEmail());
        verify(jwtTokenProvider).validateToken(token);
        verify(jwtTokenProvider).getEmailFromToken(token);
        verify(jwtTokenProvider).generateToken(user.getEmail());
    }

    @Test
    void refreshToken_InvalidToken_ThrowsException() {
        // Given
        String token = "invalid-token";
        when(jwtTokenProvider.validateToken(token)).thenReturn(false);

        // When & Then
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, 
            () -> authService.refreshToken(token));
        
        assertEquals("Invalid token", exception.getMessage());
        verify(jwtTokenProvider).validateToken(token);
        verify(jwtTokenProvider, never()).getEmailFromToken(any());
    }
}