package com.bounteous.bug_bounty_backend.services;

import com.bounteous.bug_bounty_backend.data.dto.requests.feedback.FeedbackRequest;
import com.bounteous.bug_bounty_backend.data.dto.responses.feedback.FeedbackResponse;
import com.bounteous.bug_bounty_backend.data.entities.bugs.Feedback;
import com.bounteous.bug_bounty_backend.data.entities.bugs.Solution;
import com.bounteous.bug_bounty_backend.data.entities.humans.Company;
import com.bounteous.bug_bounty_backend.data.repositories.bugs.FeedbackRepository;
import com.bounteous.bug_bounty_backend.data.repositories.bugs.SolutionRepository;
import com.bounteous.bug_bounty_backend.data.repositories.humans.CompanyRepository;
import com.bounteous.bug_bounty_backend.exceptions.ForbiddenException;
import com.bounteous.bug_bounty_backend.exceptions.ResourceNotFoundException;
import com.bounteous.bug_bounty_backend.exceptions.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final CompanyRepository companyRepository;
    private final SolutionRepository solutionRepository;
    private final FeedbackRepository feedbackRepository;

    @Transactional
    public void submitFeedback(FeedbackRequest request, String companyEmail) {
        Company company = companyRepository.findByEmail(companyEmail)
                .orElseThrow(() -> new UnauthorizedException("Invalid company"));

        Solution solution = solutionRepository.findById(request.getSolutionId())
                .orElseThrow(() -> new ResourceNotFoundException("Solution not found"));

        if (!solution.getBug().getPublisher().getId().equals(company.getId())) {
            throw new ForbiddenException("You are not authorized to give feedback on this solution.");
        }

        Feedback feedback = Feedback.builder()
                .solution(solution)
                .company(company)
                .feedbackMessage(request.getFeedbackMessage()) // uses your field name
                .rating(request.getRating())
                .submittedAt(LocalDateTime.now())
                .build();

        feedbackRepository.save(feedback);
    }
    @Transactional(readOnly = true)
    public List<FeedbackResponse> getFeedbackBySolutionId(Long solutionId) {
        Solution solution = solutionRepository.findById(solutionId)
                .orElseThrow(() -> new ResourceNotFoundException("Solution not found"));

        return solution.getFeedbacks().stream()
                .map(fb -> FeedbackResponse.builder()
                        .id(fb.getId())
                        .feedbackMessage(fb.getFeedbackMessage())
                        .rating(fb.getRating())
                        .submittedAt(fb.getSubmittedAt())
                        .company(FeedbackResponse.CompanyInfo.builder()
                                .id(fb.getCompany().getId())
                                .companyName(fb.getCompany().getCompanyName())
                                .email(fb.getCompany().getEmail())
                                .build())
                        .build())
                .toList();
    }
}
