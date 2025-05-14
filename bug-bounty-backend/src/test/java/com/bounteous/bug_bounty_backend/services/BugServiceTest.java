package com.bounteous.bug_bounty_backend.services;

import com.bounteous.bug_bounty_backend.data.dto.requests.bug.BugCreateRequest;
import com.bounteous.bug_bounty_backend.data.dto.responses.bug.BugResponse;
import com.bounteous.bug_bounty_backend.data.entities.bugs.*;
import com.bounteous.bug_bounty_backend.data.entities.humans.Company;
import com.bounteous.bug_bounty_backend.data.entities.humans.Developer;
import com.bounteous.bug_bounty_backend.data.repositories.bugs.BugRepository;
import com.bounteous.bug_bounty_backend.data.repositories.bugs.TechStackRepository;
import com.bounteous.bug_bounty_backend.data.repositories.humans.CompanyRepository;
import com.bounteous.bug_bounty_backend.data.repositories.humans.UserRepository;
import com.bounteous.bug_bounty_backend.exceptions.BadRequestException;
import com.bounteous.bug_bounty_backend.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BugServiceTest {

    @Mock
    private BugRepository bugRepository;

    @Mock
    private TechStackRepository techStackRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BugService bugService;

    private Bug bug;
    private Company company;
    private TechStack techStack;
    private BugCreateRequest bugCreateRequest;
    private List<Bug> bugs;
    private Page<Bug> bugPage;

    @BeforeEach
    void setUp() {
        // Set up company
        company = Company.builder()
                .id(1L)
                .email("company@example.com")
                .companyName("Example Corp")
                .role("COMPANY")
                .build();

        // Set up tech stack
        techStack = TechStack.builder()
                .id(1L)
                .name("Java")
                .category("Backend")
                .bugs(new ArrayList<>())
                .build();

        // Set up bug
        bug = Bug.builder()
                .id(1L)
                .title("Fix login bug")
                .description("There is a bug in the login system")
                .difficulty(Difficulty.MEDIUM)
                .reward(100.0)
                .bugStatus(BugStatus.OPEN)
                .verificationStatus(VerificationStatus.VERIFIED)
                .createdAt(LocalDateTime.now())
                .publisher(company)
                .stack(List.of(techStack))
                .build();

        // Set up bug create request
        bugCreateRequest = BugCreateRequest.builder()
                .title("Fix login bug")
                .description("There is a bug in the login system that needs fixing")
                .difficulty("MEDIUM")
                .reward(100.0)
                .techStackIds(List.of(1L))
                .build();

        // Set up bug list and page
        bugs = List.of(bug);
        bugPage = new PageImpl<>(bugs, PageRequest.of(0, 10), 1);
    }

    // Test browsing bugs (Requirement 3)
    @Test
    void findBugs_WithoutFilters_ReturnsAllBugs() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        when(bugRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(bugPage);

        // When
        Page<BugResponse> result = bugService.findBugs(null, null, null, null, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        BugResponse bugResponse = result.getContent().get(0);
        assertEquals(bug.getId(), bugResponse.getId());
        assertEquals(bug.getTitle(), bugResponse.getTitle());
        assertEquals(bug.getDifficulty().name(), bugResponse.getDifficulty());
        assertEquals(bug.getReward(), bugResponse.getReward());

        verify(bugRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void findBugs_WithDifficultyFilter_ReturnsFilteredBugs() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        when(bugRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(bugPage);

        // When
        Page<BugResponse> result = bugService.findBugs("MEDIUM", null, null, null, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(bugRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void findBugs_WithTechStackFilter_ReturnsFilteredBugs() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Long> techStackIds = List.of(1L);
        when(bugRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(bugPage);

        // When
        Page<BugResponse> result = bugService.findBugs(null, techStackIds, null, null, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(bugRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void findBugs_WithStatusFilter_ReturnsFilteredBugs() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        when(bugRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(bugPage);

        // When
        Page<BugResponse> result = bugService.findBugs(null, null, "OPEN", null, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(bugRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void findBugs_WithSearchQuery_ReturnsFilteredBugs() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        when(bugRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(bugPage);

        // When
        Page<BugResponse> result = bugService.findBugs(null, null, null, "login", pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(bugRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getBugById_ExistingBug_ReturnsBug() {
        // Given
        when(bugRepository.findById(1L)).thenReturn(Optional.of(bug));

        // When
        BugResponse result = bugService.getBugById(1L);

        // Then
        assertNotNull(result);
        assertEquals(bug.getId(), result.getId());
        assertEquals(bug.getTitle(), result.getTitle());
        assertEquals(bug.getDescription(), result.getDescription());
        assertEquals(bug.getDifficulty().name(), result.getDifficulty());
        assertEquals(bug.getReward(), result.getReward());
        assertEquals(bug.getBugStatus().name(), result.getStatus());

        verify(bugRepository).findById(1L);
    }

    @Test
    void getBugById_NonExistingBug_ThrowsException() {
        // Given
        when(bugRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> bugService.getBugById(1L));

        assertEquals("Bug not found with id: 1", exception.getMessage());
        verify(bugRepository).findById(1L);
    }

    @Test
    void getAllDifficulties_ReturnsAllDifficultyLevels() {
        // When
        List<String> result = bugService.getAllDifficulties();

        // Then
        assertNotNull(result);
        assertEquals(4, result.size());
        assertTrue(result.contains("EASY"));
        assertTrue(result.contains("MEDIUM"));
        assertTrue(result.contains("HARD"));
        assertTrue(result.contains("EXPERT"));
    }

    @Test
    void getAllTechStacks_ReturnsAllTechStacks() {
        // Given
        List<TechStack> techStacks = List.of(techStack);
        when(techStackRepository.findAll()).thenReturn(techStacks);

        // When
        List<BugResponse.TechStackInfo> result = bugService.getAllTechStacks();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        BugResponse.TechStackInfo techStackInfo = result.get(0);
        assertEquals(techStack.getId(), techStackInfo.getId());
        assertEquals(techStack.getName(), techStackInfo.getName());
        assertEquals(techStack.getCategory(), techStackInfo.getCategory());

        verify(techStackRepository).findAll();
    }

    // Test bug submission by companies (Requirement 4)
    @Test
    void createBug_ValidRequest_CreatesBug() {
        // Given
        when(companyRepository.findByEmail(company.getEmail())).thenReturn(Optional.of(company));
        when(techStackRepository.findAllById(bugCreateRequest.getTechStackIds())).thenReturn(List.of(techStack));
        when(bugRepository.save(any(Bug.class))).thenReturn(bug);
        when(techStackRepository.saveAll(any())).thenReturn(List.of(techStack));

        // When
        BugResponse result = bugService.createBug(bugCreateRequest, company.getEmail());

        // Then
        assertNotNull(result);
        assertEquals(bug.getTitle(), result.getTitle());
        assertEquals(bug.getDescription(), result.getDescription());
        assertEquals(bug.getDifficulty().name(), result.getDifficulty());
        assertEquals(bug.getReward(), result.getReward());
        assertEquals(bug.getBugStatus().name(), result.getStatus());
        assertEquals(bug.getVerificationStatus().name(), result.getVerificationStatus());

        verify(companyRepository).findByEmail(company.getEmail());
        verify(techStackRepository).findAllById(bugCreateRequest.getTechStackIds());
        verify(bugRepository).save(any(Bug.class));
        verify(techStackRepository).saveAll(any());
    }

    @Test
    void createBug_CompanyNotFound_ThrowsException() {
        // Given
        when(companyRepository.findByEmail(company.getEmail())).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> bugService.createBug(bugCreateRequest, company.getEmail()));

        assertEquals("Company not found with email: " + company.getEmail(), exception.getMessage());
        verify(companyRepository).findByEmail(company.getEmail());
        verify(bugRepository, never()).save(any());
    }

    @Test
    void createBug_InvalidDifficulty_ThrowsException() {
        // Given
        bugCreateRequest.setDifficulty("INVALID");
        when(companyRepository.findByEmail(company.getEmail())).thenReturn(Optional.of(company));
        when(techStackRepository.findAllById(bugCreateRequest.getTechStackIds())).thenReturn(List.of(techStack));

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> bugService.createBug(bugCreateRequest, company.getEmail()));

        assertEquals("Invalid difficulty level: INVALID", exception.getMessage());
        verify(companyRepository).findByEmail(company.getEmail());
        verify(bugRepository, never()).save(any());
    }

    @Test
    void createBug_TechStackNotFound_ThrowsException() {
        // Given
        when(companyRepository.findByEmail(company.getEmail())).thenReturn(Optional.of(company));
        when(techStackRepository.findAllById(bugCreateRequest.getTechStackIds())).thenReturn(List.of()); // Empty list

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> bugService.createBug(bugCreateRequest, company.getEmail()));

        assertEquals("One or more tech stacks not found", exception.getMessage());
        verify(companyRepository).findByEmail(company.getEmail());
        verify(techStackRepository).findAllById(bugCreateRequest.getTechStackIds());
        verify(bugRepository, never()).save(any());
    }

    // Test setting difficulty and reward (Requirement 5)
    @Test
    void createBug_WithValidReward_Success() {
        // Given
        bugCreateRequest.setReward(50.0); // Minimum reward
        when(companyRepository.findByEmail(company.getEmail())).thenReturn(Optional.of(company));
        when(techStackRepository.findAllById(bugCreateRequest.getTechStackIds())).thenReturn(List.of(techStack));
        when(bugRepository.save(any(Bug.class))).thenReturn(bug);
        when(techStackRepository.saveAll(any())).thenReturn(List.of(techStack));

        // When
        BugResponse result = bugService.createBug(bugCreateRequest, company.getEmail());

        // Then
        assertNotNull(result);
        assertEquals(bug.getReward(), result.getReward());
        verify(bugRepository).save(any(Bug.class));
    }

    @Test
    void createBug_WithAllDifficultyLevels_Success() {
        // Given
        when(companyRepository.findByEmail(company.getEmail())).thenReturn(Optional.of(company));
        when(techStackRepository.findAllById(bugCreateRequest.getTechStackIds())).thenReturn(List.of(techStack));
        when(techStackRepository.saveAll(any())).thenReturn(List.of(techStack));

        // Test all difficulty levels
        String[] difficulties = { "EASY", "MEDIUM", "HARD", "EXPERT" };

        for (String difficulty : difficulties) {
            // Create a new bug with the correct difficulty for each iteration
            Bug bugWithDifficulty = Bug.builder()
                    .id(1L)
                    .title("Fix login bug")
                    .description("There is a bug in the login system")
                    .difficulty(Difficulty.valueOf(difficulty)) // Set the correct difficulty
                    .reward(100.0)
                    .bugStatus(BugStatus.OPEN)
                    .verificationStatus(VerificationStatus.VERIFIED)
                    .createdAt(LocalDateTime.now())
                    .publisher(company)
                    .stack(List.of(techStack))
                    .build();

            // Mock to return the bug with correct difficulty
            when(bugRepository.save(any(Bug.class))).thenReturn(bugWithDifficulty);

            // When
            bugCreateRequest.setDifficulty(difficulty);
            BugResponse result = bugService.createBug(bugCreateRequest, company.getEmail());

            // Then
            assertNotNull(result);
            assertEquals(difficulty, result.getDifficulty());
        }

        verify(bugRepository, times(4)).save(any(Bug.class));
    }

    @Test
    void getUploadedBugsByCompany_ValidCompany_ReturnsBugs() {
        // Given
        when(bugRepository.findByPublisher(company)).thenReturn(bugs);

        // When
        List<BugResponse> result = bugService.getUploadedBugsByCompany(company);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(bug.getId(), result.get(0).getId());
        assertEquals(bug.getTitle(), result.get(0).getTitle());

        verify(bugRepository).findByPublisher(company);
    }

    @Test
    void getUploadedBugsByCompanyIdentifier_CompanyExists_ReturnsBugs() {
        // Given - Supprimez le mock de companyRepository car userRepository suffit
        when(userRepository.findByEmail(company.getEmail())).thenReturn(Optional.of(company));
        when(bugRepository.findByPublisher(company)).thenReturn(bugs);
    
        // When
        List<BugResponse> result = bugService.getUploadedBugsByCompanyIdentifier(company.getEmail());
    
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository).findByEmail(company.getEmail());
        verify(bugRepository).findByPublisher(company);
    }
    
    @Test
    void getUploadedBugsByCompanyIdentifier_NonCompanyUser_ReturnsEmptyList() {
        // Given
        Developer developer = Developer.builder()
                .id(2L)
                .email("dev@example.com")
                .role("DEVELOPER")
                .build();
        when(userRepository.findByEmail("dev@example.com")).thenReturn(Optional.of(developer));
    
        // When
        List<BugResponse> result = bugService.getUploadedBugsByCompanyIdentifier("dev@example.com");
    
        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository).findByEmail("dev@example.com");
        verify(bugRepository, never()).findByPublisher(any());
    }
}