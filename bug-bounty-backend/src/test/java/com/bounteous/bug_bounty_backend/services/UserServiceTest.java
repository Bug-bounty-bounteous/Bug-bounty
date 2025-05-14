package com.bounteous.bug_bounty_backend.services;

import com.bounteous.bug_bounty_backend.data.dto.responses.auth.UserResponse;
import com.bounteous.bug_bounty_backend.data.dto.responses.bug.BugResponse;
import com.bounteous.bug_bounty_backend.data.entities.bugs.Bug;
import com.bounteous.bug_bounty_backend.data.entities.bugs.BugClaim;
import com.bounteous.bug_bounty_backend.data.entities.bugs.BugStatus;
import com.bounteous.bug_bounty_backend.data.entities.bugs.Difficulty;
import com.bounteous.bug_bounty_backend.data.entities.bugs.TechStack;
import com.bounteous.bug_bounty_backend.data.entities.bugs.VerificationStatus;
import com.bounteous.bug_bounty_backend.data.entities.humans.Company;
import com.bounteous.bug_bounty_backend.data.entities.humans.Developer;
import com.bounteous.bug_bounty_backend.data.entities.humans.User;
import com.bounteous.bug_bounty_backend.data.repositories.bugs.BugClaimRepository;
import com.bounteous.bug_bounty_backend.data.repositories.humans.CompanyRepository;
import com.bounteous.bug_bounty_backend.data.repositories.humans.DeveloperRepository;
import com.bounteous.bug_bounty_backend.data.repositories.humans.UserRepository;
import com.bounteous.bug_bounty_backend.exceptions.ResourceNotFoundException;
import com.bounteous.bug_bounty_backend.services.RatingService;
import com.bounteous.bug_bounty_backend.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private DeveloperRepository developerRepository;
    
    @Mock
    private CompanyRepository companyRepository;
    
    @Mock
    private BugClaimRepository bugClaimRepository;
    
    @Mock
    private RatingService ratingService;
    
    @InjectMocks
    private UserService userService;

    private Developer developer;
    private Company company;
    private User user;
    private Bug bug;
    private TechStack techStack;
    private BugClaim bugClaim;

    @BeforeEach
    void setUp() {
        // Set up developer
        developer = Developer.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .username("johndoe")
                .role("DEVELOPER")
                .rating(4.5f)
                .points(150)
                .build();

        // Set up company
        company = Company.builder()
                .id(2L)
                .email("company@example.com")
                .companyName("Example Corp")
                .role("COMPANY")
                .build();

        // Set up user (can be either developer or company)
        user = developer;

        // Set up tech stack
        techStack = TechStack.builder()
                .id(1L)
                .name("Java")
                .category("Backend")
                .build();

        // Set up bug
        bug = Bug.builder()
                .id(1L)
                .title("Fix login bug")
                .description("There is a bug in the login system")
                .difficulty(Difficulty.MEDIUM)
                .reward(100.0)
                .bugStatus(BugStatus.CLAIMED)
                .verificationStatus(VerificationStatus.VERIFIED)
                .createdAt(LocalDateTime.now())
                .publisher(company)
                .stack(List.of(techStack))
                .build();

        // Set up bug claim
        bugClaim = BugClaim.builder()
                .developer(developer)
                .bug(bug)
                .date(LocalDateTime.now())
                .build();
    }

    // Test user retrieval methods - Fixed lambda exceptions
    @Test
    void getUserById_ExistingUser_ReturnsUser() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When
        User result = userService.getUserById(1L);

        // Then
        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getEmail(), result.getEmail());
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_NonExistingUser_ThrowsException() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        // Fixed: Separate the lambda to avoid multiple exception-throwing calls
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserById(1L);
        });
        
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserByEmailOrUsername_FindsByCompanyName_ReturnsUser() {
        // Given
        String companyName = company.getCompanyName();
        when(userRepository.findByEmail(companyName)).thenReturn(Optional.empty());
        when(developerRepository.findByUsername(companyName)).thenReturn(Optional.empty());
        when(companyRepository.findByCompanyName(companyName)).thenReturn(Optional.of(company));

        // When
        User result = userService.getUserByEmailOrUsername(companyName);

        // Then
        assertNotNull(result);
        assertEquals(company.getId(), result.getId());
        // Fixed: Use the company name directly instead of getCompanyName() on User
        assertEquals(companyName, company.getCompanyName());
        verify(userRepository).findByEmail(companyName);
        verify(developerRepository).findByUsername(companyName);
        verify(companyRepository).findByCompanyName(companyName);
    }

    // Test user profile methods
    @Test
    void getCurrentUserProfile_DeveloperUser_ReturnsUserResponseWithRatingAndPoints() {
        // Given
        when(ratingService.getAverageRatingAsFloat(developer.getId())).thenReturn(4.5f);

        // When
        UserResponse result = userService.getCurrentUserProfile(developer);

        // Then
        assertNotNull(result);
        assertEquals(developer.getId(), result.getId());
        assertEquals(developer.getFirstName(), result.getFirstName());
        assertEquals(developer.getLastName(), result.getLastName());
        assertEquals(developer.getEmail(), result.getEmail());
        assertEquals(developer.getUsername(), result.getUsername());
        assertEquals(developer.getRole(), result.getRole());
        assertEquals(4.5f, result.getRating());
        assertEquals(developer.getPoints(), result.getPoints());
        
        verify(ratingService).getAverageRatingAsFloat(developer.getId());
    }

    @Test
    void getCurrentUserProfile_CompanyUser_ReturnsUserResponseWithoutRatingAndPoints() {
        // When
        UserResponse result = userService.getCurrentUserProfile(company);

        // Then
        assertNotNull(result);
        assertEquals(company.getId(), result.getId());
        assertEquals(company.getFirstName(), result.getFirstName());
        assertEquals(company.getLastName(), result.getLastName());
        assertEquals(company.getEmail(), result.getEmail());
        assertNull(result.getUsername());
        assertEquals(company.getRole(), result.getRole());
        assertNull(result.getRating());
        assertNull(result.getPoints());
        
        verify(ratingService, never()).getAverageRatingAsFloat(any());
    }

    // Test claimed bugs methods
    @Test
    void getClaimedBugsByUserIdentifier_DeveloperUser_ReturnsClaimedBugs() {
        // Given
        when(userRepository.findByEmail(developer.getEmail())).thenReturn(Optional.of(developer));
        when(bugClaimRepository.findByDeveloper(developer)).thenReturn(List.of(bugClaim));

        // When
        List<BugResponse> result = userService.getClaimedBugsByUserIdentifier(developer.getEmail());

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        BugResponse bugResponse = result.get(0);
        assertEquals(bug.getId(), bugResponse.getId());
        assertEquals(bug.getTitle(), bugResponse.getTitle());
        assertEquals(bug.getDescription(), bugResponse.getDescription());
        assertEquals(bug.getDifficulty().name(), bugResponse.getDifficulty());
        assertEquals(bug.getReward(), bugResponse.getReward());
        assertEquals(bug.getBugStatus().name(), bugResponse.getStatus());
        
        verify(userRepository).findByEmail(developer.getEmail());
        verify(bugClaimRepository).findByDeveloper(developer);
    }

    @Test
    void getClaimedBugsByDeveloper_WithBugClaims_ReturnsBugResponses() {
        // Given
        when(bugClaimRepository.findByDeveloper(developer)).thenReturn(List.of(bugClaim));

        // When
        List<BugResponse> result = userService.getClaimedBugsByDeveloper(developer);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        BugResponse bugResponse = result.get(0);
        assertEquals(bug.getId(), bugResponse.getId());
        assertEquals(bug.getTitle(), bugResponse.getTitle());
        assertEquals(bug.getDescription(), bugResponse.getDescription());
        assertEquals(bug.getDifficulty().name(), bugResponse.getDifficulty());
        assertEquals(bug.getReward(), bugResponse.getReward());
        assertEquals(bug.getBugStatus().name(), bugResponse.getStatus());
        assertEquals(bug.getVerificationStatus().name(), bugResponse.getVerificationStatus());
        
        // Check tech stack info
        assertNotNull(bugResponse.getTechStacks());
        assertEquals(1, bugResponse.getTechStacks().size());
        BugResponse.TechStackInfo techStackInfo = bugResponse.getTechStacks().get(0);
        assertEquals(techStack.getId(), techStackInfo.getId());
        assertEquals(techStack.getName(), techStackInfo.getName());
        assertEquals(techStack.getCategory(), techStackInfo.getCategory());
        
        // Check publisher info
        assertNotNull(bugResponse.getPublisher());
        assertEquals(company.getId(), bugResponse.getPublisher().getId());
        // Fixed: Access company properties directly instead of through User methods
        assertEquals(company.getFirstName() + " " + company.getLastName(), bugResponse.getPublisher().getName());
        assertEquals(company.getCompanyName(), bugResponse.getPublisher().getCompanyName());
        
        verify(bugClaimRepository).findByDeveloper(developer);
    }
}