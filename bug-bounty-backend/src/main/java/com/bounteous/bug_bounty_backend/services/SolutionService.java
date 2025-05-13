package com.bounteous.bug_bounty_backend.services;

import com.bounteous.bug_bounty_backend.data.dto.requests.solution.SolutionRequest;
import com.bounteous.bug_bounty_backend.data.dto.responses.solution.SolutionResponse;
import com.bounteous.bug_bounty_backend.data.entities.bugs.Bug;
import com.bounteous.bug_bounty_backend.data.entities.bugs.Solution;
import com.bounteous.bug_bounty_backend.data.entities.bugs.SolutionStatus;
import com.bounteous.bug_bounty_backend.data.entities.humans.Developer;
import com.bounteous.bug_bounty_backend.data.repositories.bugs.BugRepository;
import com.bounteous.bug_bounty_backend.data.repositories.bugs.SolutionRepository;
import com.bounteous.bug_bounty_backend.data.repositories.humans.DeveloperRepository;
import com.bounteous.bug_bounty_backend.exceptions.ForbiddenException;
import com.bounteous.bug_bounty_backend.exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SolutionService {
// Service methods will be added here

private final BugRepository bugRepository;
private final DeveloperRepository developerRepository;
private final SolutionRepository solutionRepository;

    @Value("${spring.servlet.multipart.max-file-size}")
    private int maxFileSizeBytes;

    public List<SolutionResponse> getSolutionsByDeveloperId(Long developerId) {
        return solutionRepository.findByDeveloper_Id(developerId).stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Transactional
    public Long postSolution(@Valid SolutionRequest request, String username) throws SQLException {
        Bug bug = bugRepository.findById(request.getBugId())
                .orElseThrow(() -> new ResourceNotFoundException("No bug with id: " + request.getBugId()));

        Developer developer = developerRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("No developer with username: " + username));

        boolean hasClaimed = developer.getBugClaims().stream()
                .anyMatch(claim -> claim.getBug().equals(bug));

        if (!hasClaimed) {
            throw new ForbiddenException("This is not your bug to solve");
        }

        if ((request.getFile() != null && request.getFile().getBytes().length >= maxFileSizeBytes)) {
            throw new ForbiddenException("This file is too large");
        }

        if ((request.getFile() == null || request.getFile().isEmpty()) &&
                (request.getCodeLink() == null || request.getCodeLink().isEmpty())) {
            throw new ForbiddenException("Either a file or a link need to be attached at minimum");
        }

        Solution solution = Solution.builder()
                .description(request.getDescription())
                .codeLink(request.getCodeLink())
                .file(request.getFile() != null ? new SerialBlob(request.getFile().getBytes()) : null)
                .status(SolutionStatus.SUBMITTED)
                .submittedAt(LocalDateTime.now())
                .developer(developer)
                .bug(bug)
                .build();

        solutionRepository.save(solution);

        bug.getSolutions().add(solution);
        developer.getSolutions().add(solution);

        return solution.getId();
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





