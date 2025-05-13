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
import com.bounteous.bug_bounty_backend.data.dto.requests.solution.SolutionRequest;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.function.BinaryOperator;

// Handles solution-related business logic
@Service
@RequiredArgsConstructor
public class SolutionService {

    @Autowired
    private BugRepository bugRepository;
    @Autowired
    private DeveloperRepository developerRepository;
    @Autowired
    private SolutionRepository solutionRepository;
    @Value("${spring.servlet.multipart.max-file-size}")
    private int maxFileSizeBytes;
    

    /**
     * Create a solution for a bug in the database, by the user with the given username.
     * @param request Request details
     * @param username Email of the user who posted the solution.
     * @return The id of the new solution
     */
    @Transactional
    public Long postSolution(@Valid SolutionRequest request, String username) throws IllegalAccessException, SQLException {
        Bug bug = bugRepository.findById(request.getBugId()).orElseThrow(
                () -> new ResourceNotFoundException("No bug with id: " + request.getBugId())
        );
        Developer developer = developerRepository.findByUsername(username).orElseThrow(
                () -> new ResourceNotFoundException("No developer with username: " + username)
        );
        // check if the bug has been claimed by the developer
        if (!developer
                .getBugClaims()
                .stream().map((e) -> e.getBug() == bug)
                .reduce(false, (a, e) -> e || a)) {
            throw new ForbiddenException("This is not your bug to solve");
        }
        if (request.getFile().getBytes().length >= maxFileSizeBytes) {
            throw new ForbiddenException("This file is too large");
        }

        if ((request.getFile() == null || request.getFile().isEmpty())
                && (request.getCodeLink() == null || request.getCodeLink().isEmpty())) {
            throw new ForbiddenException("Either a file or a link need to be attached at minimum");
        }

        Solution solution = Solution.builder()
                .description(request.getDescription())
                .codeLink(request.getCodeLink())
                .file(new SerialBlob(request.getFile().getBytes()))
                .status(SolutionStatus.SUBMITTED)
                .submittedAt(LocalDateTime.now())
                .developer(developer)
                .bug(bug)
                .build();
        System.out.println(solution);
        solutionRepository.save(solution);
        bug.getSolutions().add(solution);
        developer.getSolutions().add(solution);
        return solution.getId();
    }
}
