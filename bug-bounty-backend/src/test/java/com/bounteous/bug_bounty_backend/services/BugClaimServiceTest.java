package com.bounteous.bug_bounty_backend.services;

import com.bounteous.bug_bounty_backend.data.dto.responses.ApiResponse;
import com.bounteous.bug_bounty_backend.data.entities.bugs.Bug;
import com.bounteous.bug_bounty_backend.data.entities.bugs.BugClaim;
import com.bounteous.bug_bounty_backend.data.entities.bugs.BugClaimId;
import com.bounteous.bug_bounty_backend.data.entities.bugs.BugStatus;
import com.bounteous.bug_bounty_backend.data.entities.bugs.ClaimStatus;
import com.bounteous.bug_bounty_backend.data.entities.bugs.Difficulty;
import com.bounteous.bug_bounty_backend.data.entities.humans.Company;
import com.bounteous.bug_bounty_backend.data.entities.humans.Developer;
import com.bounteous.bug_bounty_backend.data.entities.humans.User;
import com.bounteous.bug_bounty_backend.data.repositories.bugs.BugClaimRepository;
import com.bounteous.bug_bounty_backend.data.repositories.bugs.BugRepository;
import com.bounteous.bug_bounty_backend.exceptions.BadRequestException;
import com.bounteous.bug_bounty_backend.exceptions.ForbiddenException;
import com.bounteous.bug_bounty_backend.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BugClaimServiceTest {

    @Mock
    private BugRepository bugRepository;
    
    @Mock
    private UserService userService;
    
    @Mock
    private BugClaimRepository bugClaimRepository;
    
    @InjectMocks
    private BugClaimService bugClaimService;

    private Bug bug;
    private Developer developer;
    private Company company;
    private BugClaim bugClaim;
    private BugClaimId bugClaimId;

    @BeforeEach
    void setUp() {
        // Set up company
        company = Company.builder()
                .id(1L)
                .email("company@example.com")
                .companyName("Example Corp")
                .role("COMPANY")
                .build();

        // Set up developer
        developer = Developer.builder()
                .id(2L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .username("johndoe")
                .role("DEVELOPER")
                .rating(0.0f)
                .points(0)
                .build();

        // Set up bug
        bug = Bug.builder()
                .id(1L)
                .title("Fix login bug")
                .description("There is a bug in the login system")
                .difficulty(Difficulty.MEDIUM)
                .reward(100.0)
                .bugStatus(BugStatus.OPEN)
                .createdAt(LocalDateTime.now())
                .publisher(company)
                .build();

        // Set up bug claim
        bugClaimId = new BugClaimId(developer.getId(), bug.getId());
        bugClaim = BugClaim.builder()
                .developer(developer)
                .bug(bug)
                .date(LocalDateTime.now())
                .claimStatus(ClaimStatus.APPROVED)
                .claimNote("I want to work on this bug")
                .build();
    }

    // Test bug claiming (Requirement 6)
    @Test
    void claimBug_ValidDeveloperAndOpenBug_Success() {
        // Given
        when(bugRepository.findById(bug.getId())).thenReturn(Optional.of(bug));
        when(userService.getUserByEmailOrUsername(developer.getEmail())).thenReturn(developer);
        when(bugClaimRepository.existsById(bugClaimId)).thenReturn(false);
        when(bugClaimRepository.save(any(BugClaim.class))).thenReturn(bugClaim);
        when(bugRepository.save(bug)).thenReturn(bug);

        // When
        ApiResponse response = bugClaimService.claimBug(bug.getId(), developer.getEmail(), "I want to work on this bug");

        // Then
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("Bug claimed successfully", response.getMessage());
        assertEquals(BugStatus.CLAIMED, bug.getBugStatus());

        verify(bugRepository).findById(bug.getId());
        verify(userService).getUserByEmailOrUsername(developer.getEmail());
        verify(bugClaimRepository).existsById(bugClaimId);
        verify(bugClaimRepository).save(any(BugClaim.class));
        verify(bugRepository).save(bug);
    }

    @Test
    void claimBug_BugNotFound_ThrowsException() {
        // Given
        when(bugRepository.findById(bug.getId())).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> bugClaimService.claimBug(bug.getId(), developer.getEmail(), "Note"));

        assertEquals("Bug not found with id: " + bug.getId(), exception.getMessage());
        verify(bugRepository).findById(bug.getId());
        verify(userService, never()).getUserByEmailOrUsername(any());
        verify(bugClaimRepository, never()).save(any());
    }

    @Test
    void claimBug_BugAlreadyClaimed_ThrowsException() {
        // Given
        bug.setBugStatus(BugStatus.CLAIMED);
        when(bugRepository.findById(bug.getId())).thenReturn(Optional.of(bug));

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class,
            () -> bugClaimService.claimBug(bug.getId(), developer.getEmail(), "Note"));

        assertEquals("This bug is already claimed or not available for claiming", exception.getMessage());
        verify(bugRepository).findById(bug.getId());
        verify(userService, never()).getUserByEmailOrUsername(any());
        verify(bugClaimRepository, never()).save(any());
    }

    @Test
    void claimBug_UserIsCompany_ThrowsException() {
        // Given
        when(bugRepository.findById(bug.getId())).thenReturn(Optional.of(bug));
        when(userService.getUserByEmailOrUsername(company.getEmail())).thenReturn(company);

        // When & Then
        ForbiddenException exception = assertThrows(ForbiddenException.class,
            () -> bugClaimService.claimBug(bug.getId(), company.getEmail(), "Note"));

        assertEquals("Only developers can claim bugs", exception.getMessage());
        verify(bugRepository).findById(bug.getId());
        verify(userService).getUserByEmailOrUsername(company.getEmail());
        verify(bugClaimRepository, never()).save(any());
    }

    @Test
    void claimBug_DeveloperTriesToClaimOwnBug_ThrowsException() {
        // Given
        bug.setPublisher(company);
        // Set developer as publisher (simulate company account)
        Developer developerAsPublisher = Developer.builder()
                .id(developer.getId())
                .email(developer.getEmail())
                .build();
        Company companyWithSameId = Company.builder()
                .id(developer.getId()) // Same ID as developer
                .email("company@example.com")
                .build();
        bug.setPublisher(companyWithSameId);

        when(bugRepository.findById(bug.getId())).thenReturn(Optional.of(bug));
        when(userService.getUserByEmailOrUsername(developer.getEmail())).thenReturn(developer);

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class,
            () -> bugClaimService.claimBug(bug.getId(), developer.getEmail(), "Note"));

        assertEquals("You cannot claim your own bugs", exception.getMessage());
        verify(bugRepository).findById(bug.getId());
        verify(userService).getUserByEmailOrUsername(developer.getEmail());
        verify(bugClaimRepository, never()).save(any());
    }

    @Test
    void claimBug_DeveloperAlreadyClaimedBug_ThrowsException() {
        // Given
        when(bugRepository.findById(bug.getId())).thenReturn(Optional.of(bug));
        when(userService.getUserByEmailOrUsername(developer.getEmail())).thenReturn(developer);
        when(bugClaimRepository.existsById(bugClaimId)).thenReturn(true);

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class,
            () -> bugClaimService.claimBug(bug.getId(), developer.getEmail(), "Note"));

        assertEquals("You have already claimed this bug", exception.getMessage());
        verify(bugRepository).findById(bug.getId());
        verify(userService).getUserByEmailOrUsername(developer.getEmail());
        verify(bugClaimRepository).existsById(bugClaimId);
        verify(bugClaimRepository, never()).save(any());
    }

    @Test
    void claimBug_ConcurrentClaim_ThrowsException() {
        // Given - Simulate race condition
        when(bugRepository.findById(bug.getId())).thenReturn(Optional.of(bug));
        when(userService.getUserByEmailOrUsername(developer.getEmail())).thenReturn(developer);
        when(bugClaimRepository.existsById(bugClaimId)).thenReturn(false);
        when(bugClaimRepository.save(any(BugClaim.class))).thenThrow(new DataIntegrityViolationException("Constraint violation"));

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class,
            () -> bugClaimService.claimBug(bug.getId(), developer.getEmail(), "Note"));

        assertEquals("This bug was just claimed by another developer", exception.getMessage());
        verify(bugRepository).findById(bug.getId());
        verify(userService).getUserByEmailOrUsername(developer.getEmail());
        verify(bugClaimRepository).existsById(bugClaimId);
        verify(bugClaimRepository).save(any(BugClaim.class));
    }

    @Test
    void claimBug_WithoutNote_Success() {
        // Given
        when(bugRepository.findById(bug.getId())).thenReturn(Optional.of(bug));
        when(userService.getUserByEmailOrUsername(developer.getEmail())).thenReturn(developer);
        when(bugClaimRepository.existsById(bugClaimId)).thenReturn(false);
        when(bugClaimRepository.save(any(BugClaim.class))).thenReturn(bugClaim);
        when(bugRepository.save(bug)).thenReturn(bug);

        // When
        ApiResponse response = bugClaimService.claimBug(bug.getId(), developer.getEmail(), null);

        // Then
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("Bug claimed successfully", response.getMessage());
        verify(bugClaimRepository).save(argThat(claim -> 
            claim.getClaimNote() == null && 
            claim.getClaimStatus() == ClaimStatus.APPROVED
        ));
    }

    // Test bug unclaiming
    @Test
    void unclaimBug_ValidDeveloperAndClaimedBug_Success() {
        // Given
        bug.setBugStatus(BugStatus.CLAIMED);
        when(bugRepository.findById(bug.getId())).thenReturn(Optional.of(bug));
        when(userService.getUserByEmailOrUsername(developer.getEmail())).thenReturn(developer);
        when(bugClaimRepository.findById(bugClaimId)).thenReturn(Optional.of(bugClaim));
        when(bugRepository.save(bug)).thenReturn(bug);

        // When
        bugClaimService.unclaimBug(bug.getId(), developer.getEmail());

        // Then
        assertEquals(BugStatus.OPEN, bug.getBugStatus());
        verify(bugRepository).findById(bug.getId());
        verify(userService).getUserByEmailOrUsername(developer.getEmail());
        verify(bugClaimRepository).findById(bugClaimId);
        verify(bugClaimRepository).delete(bugClaim);
        verify(bugRepository).save(bug);
    }

    @Test
    void unclaimBug_BugNotFound_ThrowsException() {
        // Given
        when(bugRepository.findById(bug.getId())).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> bugClaimService.unclaimBug(bug.getId(), developer.getEmail()));

        assertEquals("Bug not found with id: " + bug.getId(), exception.getMessage());
        verify(bugRepository).findById(bug.getId());
        verify(userService, never()).getUserByEmailOrUsername(any());
        verify(bugClaimRepository, never()).delete(any());
    }

    @Test
    void unclaimBug_BugNotClaimed_ThrowsException() {
        // Given
        bug.setBugStatus(BugStatus.OPEN);
        when(bugRepository.findById(bug.getId())).thenReturn(Optional.of(bug));

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class,
            () -> bugClaimService.unclaimBug(bug.getId(), developer.getEmail()));

        assertEquals("This bug is not claimed or available for claiming", exception.getMessage());
        verify(bugRepository).findById(bug.getId());
        verify(userService, never()).getUserByEmailOrUsername(any());
        verify(bugClaimRepository, never()).delete(any());
    }

    @Test
    void unclaimBug_UserIsCompany_ThrowsException() {
        // Given
        bug.setBugStatus(BugStatus.CLAIMED);
        when(bugRepository.findById(bug.getId())).thenReturn(Optional.of(bug));
        when(userService.getUserByEmailOrUsername(company.getEmail())).thenReturn(company);

        // When & Then
        ForbiddenException exception = assertThrows(ForbiddenException.class,
            () -> bugClaimService.unclaimBug(bug.getId(), company.getEmail()));

        assertEquals("Only developers can unclaim bugs", exception.getMessage());
        verify(bugRepository).findById(bug.getId());
        verify(userService).getUserByEmailOrUsername(company.getEmail());
        verify(bugClaimRepository, never()).delete(any());
    }

    @Test
    void unclaimBug_DeveloperDidNotClaimBug_ThrowsException() {
        // Given
        bug.setBugStatus(BugStatus.CLAIMED);
        when(bugRepository.findById(bug.getId())).thenReturn(Optional.of(bug));
        when(userService.getUserByEmailOrUsername(developer.getEmail())).thenReturn(developer);
        when(bugClaimRepository.findById(bugClaimId)).thenReturn(Optional.empty());

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class,
            () -> bugClaimService.unclaimBug(bug.getId(), developer.getEmail()));

        assertEquals("Bug claim not found for this user", exception.getMessage());
        verify(bugRepository).findById(bug.getId());
        verify(userService).getUserByEmailOrUsername(developer.getEmail());
        verify(bugClaimRepository).findById(bugClaimId);
        verify(bugClaimRepository, never()).delete(any());
    }

    @Test
    void unclaimBug_ConcurrentUnclaim_ThrowsException() {
        // Given - Simulate race condition
        bug.setBugStatus(BugStatus.CLAIMED);
        when(bugRepository.findById(bug.getId())).thenReturn(Optional.of(bug));
        when(userService.getUserByEmailOrUsername(developer.getEmail())).thenReturn(developer);
        when(bugClaimRepository.findById(bugClaimId)).thenReturn(Optional.of(bugClaim));
        doThrow(new DataIntegrityViolationException("Constraint violation")).when(bugClaimRepository).delete(bugClaim);

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class,
            () -> bugClaimService.unclaimBug(bug.getId(), developer.getEmail()));

        assertEquals("This bug was just claimed by another developer", exception.getMessage());
        verify(bugRepository).findById(bug.getId());
        verify(userService).getUserByEmailOrUsername(developer.getEmail());
        verify(bugClaimRepository).findById(bugClaimId);
        verify(bugClaimRepository).delete(bugClaim);
    }
}