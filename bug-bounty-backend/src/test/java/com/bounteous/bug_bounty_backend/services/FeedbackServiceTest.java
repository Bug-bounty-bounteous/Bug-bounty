package com.bounteous.bug_bounty_backend.services;

import com.bounteous.bug_bounty_backend.data.dto.requests.feedback.FeedbackRequest;
import com.bounteous.bug_bounty_backend.data.dto.responses.feedback.FeedbackResponse;
import com.bounteous.bug_bounty_backend.data.entities.bugs.Bug;
import com.bounteous.bug_bounty_backend.data.entities.bugs.Feedback;
import com.bounteous.bug_bounty_backend.data.entities.bugs.Solution;
import com.bounteous.bug_bounty_backend.data.entities.humans.Company;
import com.bounteous.bug_bounty_backend.data.repositories.bugs.FeedbackRepository;
import com.bounteous.bug_bounty_backend.data.repositories.bugs.SolutionRepository;
import com.bounteous.bug_bounty_backend.data.repositories.humans.CompanyRepository;
import com.bounteous.bug_bounty_backend.exceptions.ForbiddenException;
import com.bounteous.bug_bounty_backend.exceptions.ResourceNotFoundException;
import com.bounteous.bug_bounty_backend.exceptions.UnauthorizedException;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedbackServiceTest {

    @Mock private CompanyRepository companyRepository;
    @Mock private SolutionRepository solutionRepository;
    @Mock private FeedbackRepository feedbackRepository;

    @InjectMocks private FeedbackService feedbackService;

    private Company company;
    private Solution solution;

    @BeforeEach
    void setUp() {
        company = Company.builder()
                .id(1L)
                .companyName("Test Company")
                .email("company@test.com")
                .build();

        solution = Solution.builder()
                .id(10L)
                .description("Test Solution")
                .bug(Bug.builder().id(5L).publisher(company).build())
                .build();
    }

    @Test
    void submitFeedback_Success() {
        FeedbackRequest request = FeedbackRequest.builder()
                .solutionId(10L)
                .feedbackMessage("Good work")
                .rating(5)
                .build();

        when(companyRepository.findByEmail(company.getEmail())).thenReturn(Optional.of(company));
        when(solutionRepository.findById(10L)).thenReturn(Optional.of(solution));
        when(feedbackRepository.save(any(Feedback.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        feedbackService.submitFeedback(request, company.getEmail());

        // Assert
        verify(companyRepository).findByEmail(company.getEmail());
        verify(solutionRepository).findById(10L);
        verify(feedbackRepository).save(any(Feedback.class));
    }

    @Test
    void submitFeedback_InvalidCompany_ThrowsUnauthorizedException() {
        when(companyRepository.findByEmail(company.getEmail())).thenReturn(Optional.empty());

        FeedbackRequest request = FeedbackRequest.builder()
                .solutionId(10L)
                .feedbackMessage("Test")
                .rating(3)
                .build();

        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () ->
                feedbackService.submitFeedback(request, company.getEmail()));

        assertEquals("Invalid company", exception.getMessage());
    }

    @Test
    void submitFeedback_SolutionNotFound_ThrowsResourceNotFoundException() {
        when(companyRepository.findByEmail(company.getEmail())).thenReturn(Optional.of(company));
        when(solutionRepository.findById(10L)).thenReturn(Optional.empty());

        FeedbackRequest request = FeedbackRequest.builder()
                .solutionId(10L)
                .feedbackMessage("Test")
                .rating(3)
                .build();

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                feedbackService.submitFeedback(request, company.getEmail()));

        assertEquals("Solution not found", exception.getMessage());
    }

    @Test
    void submitFeedback_CompanyNotOwner_ThrowsForbiddenException() {
        Company otherCompany = Company.builder()
                .id(2L)
                .companyName("Other Company")
                .email("other@test.com")
                .build();

        Solution otherSolution = Solution.builder()
                .id(10L)
                .bug(Bug.builder().id(5L).publisher(otherCompany).build())
                .build();

        when(companyRepository.findByEmail(company.getEmail())).thenReturn(Optional.of(company));
        when(solutionRepository.findById(10L)).thenReturn(Optional.of(otherSolution));

        FeedbackRequest request = FeedbackRequest.builder()
                .solutionId(10L)
                .feedbackMessage("Test")
                .rating(3)
                .build();

        ForbiddenException exception = assertThrows(ForbiddenException.class, () ->
                feedbackService.submitFeedback(request, company.getEmail()));

        assertEquals("You are not authorized to give feedback on this solution.", exception.getMessage());
    }

    @Test
    void getFeedbackBySolutionId_Success() {
        Feedback feedback = Feedback.builder()
                .id(1L)
                .feedbackMessage("Nice fix")
                .rating(5)
                .submittedAt(LocalDateTime.now())
                .company(company)
                .solution(solution)
                .build();

        solution.setFeedbacks(List.of(feedback));

        when(solutionRepository.findById(solution.getId())).thenReturn(Optional.of(solution));

        List<FeedbackResponse> responses = feedbackService.getFeedbackBySolutionId(solution.getId());

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Nice fix", responses.get(0).getFeedbackMessage());
        assertEquals(5, responses.get(0).getRating());
        assertEquals(company.getCompanyName(), responses.get(0).getCompany().getCompanyName());
    }

    @Test
    void getFeedbackBySolutionId_SolutionNotFound_ThrowsResourceNotFoundException() {
        when(solutionRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                feedbackService.getFeedbackBySolutionId(99L));

        assertEquals("Solution not found", exception.getMessage());
    }
}
