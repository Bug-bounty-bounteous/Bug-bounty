package com.bounteous.bug_bounty_backend.services;

import com.bounteous.bug_bounty_backend.data.dto.requests.solution.SolutionRequest;
import com.bounteous.bug_bounty_backend.data.dto.responses.solution.SolutionResponse;
import com.bounteous.bug_bounty_backend.data.entities.bugs.Bug;
import com.bounteous.bug_bounty_backend.data.entities.bugs.BugStatus;
import com.bounteous.bug_bounty_backend.data.entities.bugs.Solution;
import com.bounteous.bug_bounty_backend.data.entities.bugs.SolutionStatus;
import com.bounteous.bug_bounty_backend.data.entities.humans.Admin;
import com.bounteous.bug_bounty_backend.data.entities.humans.Company;
import com.bounteous.bug_bounty_backend.data.entities.humans.Developer;
import com.bounteous.bug_bounty_backend.data.entities.humans.User;
import com.bounteous.bug_bounty_backend.data.repositories.bugs.BugRepository;
import com.bounteous.bug_bounty_backend.data.repositories.bugs.SolutionRepository;
import com.bounteous.bug_bounty_backend.data.repositories.humans.CompanyRepository;
import com.bounteous.bug_bounty_backend.data.repositories.humans.DeveloperRepository;
import com.bounteous.bug_bounty_backend.data.repositories.humans.UserRepository;
import com.bounteous.bug_bounty_backend.exceptions.BadRequestException;
import com.bounteous.bug_bounty_backend.exceptions.ForbiddenException;
import com.bounteous.bug_bounty_backend.exceptions.ResourceNotFoundException;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.stereotype.Service;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private UserRepository userRepository;
    @Value("${spring.servlet.multipart.max-file-size}")
    private int maxFileSizeBytes;



    /**
     * Create a solution for a bug in the database, by the user with the given username.
     * @param request Request details
     * @param email Email of the user who posted the solution.
     * @return The id of the new solution
     */
    @Transactional
    public Long postSolution(@Valid SolutionRequest request, String email) throws IllegalAccessException, SQLException {
        Bug bug = bugRepository.findById(request.getBugId()).orElseThrow(
                () -> new ResourceNotFoundException("No bug with id: " + request.getBugId())
        );
        Developer developer = developerRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("No developer with email: " + email)
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
                .filename(request.getFilename())
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
    @Transactional(readOnly = true)
    public List<SolutionResponse> getSolutionsByDeveloperId(Long developerId) {
        List<Solution> solutions = solutionRepository.findByDeveloper_Id(developerId);

        return solutions.stream().map(solution -> SolutionResponse.builder()
                .id(solution.getId())
                .description(solution.getDescription())
                .codeLink(solution.getCodeLink())
                .status(solution.getStatus().toString())
                .submittedAt(solution.getSubmittedAt())
                .reviewedAt(solution.getReviewedAt())
                .bug(SolutionResponse.BugInfo.builder()
                        .id(solution.getBug().getId())
                        .title(solution.getBug().getTitle())
                        .build())
                .developer(SolutionResponse.DeveloperInfo.builder()
                        .id(solution.getDeveloper().getId())
                        .username(solution.getDeveloper().getUsername())
                        .email(solution.getDeveloper().getEmail())
                        .rating(solution.getDeveloper().getRating())
                        .build())
                .build()
        ).toList();
    }

    public SolutionResponse getSolutionById(Long solutionId) {
        Solution solution = solutionRepository.findById(solutionId).orElseThrow(
                () -> new BadRequestException("Solution with id ")
        );
        return SolutionResponse.builder()
                .id(solution.getId())
                .description(solution.getDescription())
                .codeLink(solution.getCodeLink())
                .status(solution.getStatus().toString())
                .submittedAt(solution.getSubmittedAt())
                .reviewedAt(solution.getReviewedAt())
                .bug(SolutionResponse.BugInfo.builder()
                        .id(solution.getBug().getId())
                        .title(solution.getBug().getTitle())
                        .build())
                .developer(SolutionResponse.DeveloperInfo.builder()
                        .id(solution.getDeveloper().getId())
                        .username(solution.getDeveloper().getUsername())
                        .email(solution.getDeveloper().getEmail())
                        .rating(solution.getDeveloper().getRating())
                        .build())
                .build();
    }

    public Pair<byte[], String> getSolutionFile(Long solutionId) throws SQLException {
        Solution solution = solutionRepository.findById(solutionId).orElseThrow(
                () -> new BadRequestException("Solution with id '" + solutionId + "' doesn't exist")
        );
        Blob blob = solution.getFile();
        return new Pair<>(blob.getBytes(1, (int) blob.length()), solution.getFilename());
    }

    public boolean companyCanMutateSolution(Company company, Solution solution) {
        Bug solutionBug = solution.getBug();
        for (Bug bug: company.getBugs()) {
            if (Objects.equals(bug.getId(), solutionBug.getId())) return true;
        }
        return false;
    }

    @Transactional
    public void setVerdict(Long solutionId, @Valid String request, String email) {
        Solution solution = solutionRepository.findById(solutionId).orElseThrow(
                () -> new BadRequestException("Solution with id ")
        );
        Company company = companyRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("No company with email: " + email)
        );
        if (!this.companyCanMutateSolution(company, solution)) {
            throw new BadRequestException("Bug '" + solution.getBug().getTitle() + "' is not from Company '"+ company.getCompanyName()+ "'");
        }
        SolutionStatus status;
        try {
            status = SolutionStatus.valueOf(request);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(request + " is not a valid SolutionStatus");
        }
        solution.setStatus(status);
        if (status == SolutionStatus.ACCEPTED) {
            Bug bug = solution.getBug();
            bug.setBugStatus(BugStatus.RESOLVED);
            bugRepository.save(bug);
            Developer claimer = solution.getDeveloper();
            claimer.setPoints(claimer.getPoints() + (int) bug.getReward());
            developerRepository.save(claimer);
        }
        solutionRepository.save(solution);
    }

    private List<SolutionResponse> getAllSolutionsForBug(Bug bug) {
        return bug.getSolutions().stream().map(
                solution ->
                    SolutionResponse.builder()
                            .id(solution.getId())
                            .description(solution.getDescription())
                            .codeLink(solution.getCodeLink())
                            .status(solution.getStatus().toString())
                            .submittedAt(solution.getSubmittedAt())
                            .reviewedAt(solution.getReviewedAt())
                            .bug(SolutionResponse.BugInfo.builder()
                                    .id(solution.getBug().getId())
                                    .title(solution.getBug().getTitle())
                                    .build())
                            .developer(SolutionResponse.DeveloperInfo.builder()
                                    .id(solution.getDeveloper().getId())
                                    .username(solution.getDeveloper().getUsername())
                                    .email(solution.getDeveloper().getEmail())
                                    .rating(solution.getDeveloper().getRating())
                                    .build())
                            .build()
        ).toList();
    }

    private List<SolutionResponse> getDeveloperSolutionsForBug(Bug bug, Developer user) {
        List<SolutionResponse> allSols = getAllSolutionsForBug(bug);
        return allSols.stream().filter(
                solutionResponse ->
                        Objects.equals(solutionResponse.getDeveloper().getId(), user.getId())
                ).toList();
    }

    public List<SolutionResponse> getSolutionsForBug(Long bugId, String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new BadRequestException(
                String.format("No user with email %s", email)
        ));
        Bug bug = bugRepository.findById(bugId).orElseThrow(
                () -> new ResourceNotFoundException(
                        String.format("No bug with id: %d", bugId))
        );

        if (user instanceof Developer
                && ((Developer) user).isClaiming(bug)) {
            return getDeveloperSolutionsForBug(bug, (Developer) user);
        }
        else if (
                (user instanceof Company
                && ((Company) user).publishedBug(bug))
                        || user instanceof Admin) {
            return getAllSolutionsForBug(bug);
        } else {
            return List.of();
        }
    }
}
