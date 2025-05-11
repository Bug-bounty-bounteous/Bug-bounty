package com.bounteous.bug_bounty_backend.services;

import com.bounteous.bug_bounty_backend.data.dto.responses.solution.SolutionResponse;
import com.bounteous.bug_bounty_backend.data.entities.bugs.Solution;
import com.bounteous.bug_bounty_backend.data.repositories.bugs.SolutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SolutionService {
// Service methods will be added here
private final SolutionRepository solutionRepository;

    public List<SolutionResponse> getSolutionsByDeveloperId(Long developerId) {
        return solutionRepository.findByDeveloper_Id(developerId).stream()
                .map(this::convertToResponse)
                .toList();
    }

    private SolutionResponse convertToResponse(Solution solution) {
        return SolutionResponse.builder()
                .id(solution.getId())
                .description(solution.getDescription())
                .codeLink(solution.getCodeLink())
                .status(solution.getStatus().name())
                .submittedAt(solution.getSubmittedAt())
                .reviewedAt(solution.getReviewedAt())
                .developer(SolutionResponse.DeveloperInfo.builder()
                        .id(solution.getDeveloper().getId())
                        .username(solution.getDeveloper().getUsername())
                        .email(solution.getDeveloper().getEmail())
                        .rating(solution.getDeveloper().getRating())
                        .build())
                .bug(SolutionResponse.BugInfo.builder()
                        .id(solution.getBug().getId())
                        .title(solution.getBug().getTitle())
                        .build())
                .build();
    }
}





