package com.bounteous.bug_bounty_backend.services;

import com.bounteous.bug_bounty_backend.data.dto.requests.solution.SolutionRequest;
import com.bounteous.bug_bounty_backend.data.entities.bugs.Bug;
import com.bounteous.bug_bounty_backend.data.entities.bugs.Solution;
import com.bounteous.bug_bounty_backend.data.entities.bugs.SolutionStatus;
import com.bounteous.bug_bounty_backend.data.entities.humans.Developer;
import com.bounteous.bug_bounty_backend.data.repositories.bugs.BugRepository;
import com.bounteous.bug_bounty_backend.data.repositories.bugs.SolutionRepository;
import com.bounteous.bug_bounty_backend.data.repositories.humans.DeveloperRepository;
import com.bounteous.bug_bounty_backend.exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

// Handles solution-related business logic
@Service
public class SolutionService {

    @Autowired
    private BugRepository bugRepository;
    @Autowired
    private DeveloperRepository developerRepository;
    @Autowired
    private SolutionRepository solutionRepository;
    

    /**
     * Create a solution for a bug in the database, by the user with the given username.
     * @param request Request details
     * @param username Email of the user who posted the solution.
     * @return The id of the new solution
     */
    @Transactional
    public Long postSolution(@Valid SolutionRequest request, String username) {
        Bug bug = bugRepository.findById(request.getBugId()).orElseThrow(
                () -> new ResourceNotFoundException("No bug with id: " + request.getBugId())
        );
        Developer developer = developerRepository.findByUsername(username).orElseThrow(
                () -> new ResourceNotFoundException("No developer with username: " + username)
        );
        Solution solution = Solution.builder()
                .description(request.getDescription())
                .codeLink(request.getCodeLink())
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
}
