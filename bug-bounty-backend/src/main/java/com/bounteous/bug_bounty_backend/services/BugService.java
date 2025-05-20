package com.bounteous.bug_bounty_backend.services;

import com.bounteous.bug_bounty_backend.data.dto.responses.bug.BugResponse;
import com.bounteous.bug_bounty_backend.data.entities.bugs.*;
import com.bounteous.bug_bounty_backend.data.repositories.bugs.BugRepository;
import com.bounteous.bug_bounty_backend.data.repositories.bugs.TechStackRepository;
import com.bounteous.bug_bounty_backend.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bounteous.bug_bounty_backend.data.dto.requests.bug.BugCreateRequest;
import com.bounteous.bug_bounty_backend.data.entities.humans.Company;
import com.bounteous.bug_bounty_backend.data.entities.humans.Developer;
import com.bounteous.bug_bounty_backend.data.entities.humans.User;
import com.bounteous.bug_bounty_backend.data.repositories.humans.CompanyRepository;
import com.bounteous.bug_bounty_backend.data.repositories.humans.DeveloperRepository;
import com.bounteous.bug_bounty_backend.data.repositories.humans.UserRepository;
import com.bounteous.bug_bounty_backend.exceptions.BadRequestException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BugService {

    private final BugRepository bugRepository;
    private final TechStackRepository techStackRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    /**
     * Find bugs with filtering options
     */
    @Transactional(readOnly = true)
    public Page<BugResponse> findBugs(String difficultyStr, List<Long> techStackIds,
            String statusStr, String query, Pageable pageable) {

        // Create specifications based on filter criteria
        Specification<Bug> spec = Specification.where(null);

        // Filter by difficulty if provided
        if (difficultyStr != null && !difficultyStr.isEmpty()) {
            try {
                Difficulty difficulty = Difficulty.valueOf(difficultyStr.toUpperCase());
                spec = spec.and((root, criteriaQuery, cb) -> cb.equal(root.get("difficulty"), difficulty));
            } catch (IllegalArgumentException e) {
                // Invalid difficulty, ignore this filter
            }
        }

        // Filter by tech stacks if provided
        if (techStackIds != null && !techStackIds.isEmpty()) {
            spec = spec.and((root, criteriaQuery, cb) -> {
                criteriaQuery.distinct(true);
                return root.join("stack").get("id").in(techStackIds);
            });
        }

        // Filter by status if provided
        if (statusStr != null && !statusStr.isEmpty()) {
            try {
                BugStatus status = BugStatus.valueOf(statusStr.toUpperCase());
                spec = spec.and((root, criteriaQuery, cb) -> cb.equal(root.get("bugStatus"), status));
            } catch (IllegalArgumentException e) {
                // Invalid status, ignore this filter
            }
        }

        // Search in title or description if query is provided
        if (query != null && !query.isEmpty()) {
            String searchTerm = "%" + query.toLowerCase() + "%";
            spec = spec.and((root, criteriaQuery, cb) -> cb.or(
                    cb.like(cb.lower(root.get("title")), searchTerm),
                    cb.like(cb.lower(root.get("description")), searchTerm)));
        }

        // Execute the query with all specifications and convert to DTOs
        return bugRepository.findAll(spec, pageable)
                .map(this::convertToResponse);
    }

    /**
     * Get a bug by ID
     */
    @Transactional(readOnly = true)
    public BugResponse getBugById(Long id) {
        Bug bug = bugRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bug not found with id: " + id));

        return convertToResponse(bug);
    }

    /**
     * Get all available difficulty levels
     */
    public List<String> getAllDifficulties() {
        return Arrays.stream(Difficulty.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    /**
     * Get all available tech stacks
     */
    @Transactional(readOnly = true)
    public List<BugResponse.TechStackInfo> getAllTechStacks() {
        return techStackRepository.findAll().stream()
                .map(techStack -> new BugResponse.TechStackInfo(
                techStack.getId(),
                techStack.getName(),
                techStack.getCategory()))
                .collect(Collectors.toList());
    }

    /**
     * Convert Bug entity to BugResponse DTO
     */
    private BugResponse convertToResponse(Bug bug) {
        List<BugResponse.TechStackInfo> techStacks = bug.getStack().stream()
                .map(techStack -> new BugResponse.TechStackInfo(
                techStack.getId(),
                techStack.getName(),
                techStack.getCategory()))
                .collect(Collectors.toList());

        BugResponse.UserInfo publisher = null;
        if (bug.getPublisher() != null) {
            publisher = new BugResponse.UserInfo(
                    bug.getPublisher().getId(),
                    bug.getPublisher().getFirstName() + " " + bug.getPublisher().getLastName(),
                    bug.getPublisher().getCompanyName());
        }

        return new BugResponse(
                bug.getId(),
                bug.getTitle(),
                bug.getDescription(),
                bug.getDifficulty().name(),
                bug.getReward(),
                bug.getBugStatus().name(),
                bug.getCreatedAt(),
                bug.getUpdatedAt(),
                publisher,
                techStacks,
                bug.getVerificationStatus() != null ? bug.getVerificationStatus().name() : null);
    }

    /**
     * Create a new bug
     */
    @Transactional
    public BugResponse createBug(BugCreateRequest request, String companyEmail) {
        // Find the company
        Company company = companyRepository.findByEmail(companyEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with email: " + companyEmail));

        // Validate tech stacks
        List<TechStack> techStacks = new ArrayList<>();
        if (request.getTechStackIds() != null && !request.getTechStackIds().isEmpty()) {
            techStacks = techStackRepository.findAllById(request.getTechStackIds());
            if (techStacks.size() != request.getTechStackIds().size()) {
                throw new BadRequestException("One or more tech stacks not found");
            }
        }

        // Create the bug
        Difficulty difficulty;
        try {
            difficulty = Difficulty.valueOf(request.getDifficulty().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid difficulty level: " + request.getDifficulty());
        }

        Bug bug = Bug.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .difficulty(difficulty)
                .reward(request.getReward())
                .bugStatus(BugStatus.OPEN)
                .verificationStatus(VerificationStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .publisher(company)
                .stack(techStacks)
                .build();

        // Save the bug
        Bug savedBug = bugRepository.save(bug);

        // Also update the tech stacks to include this bug
        for (TechStack techStack : techStacks) {
            techStack.getBugs().add(savedBug);
        }
        techStackRepository.saveAll(techStacks);

        return convertToResponse(savedBug);
    }

    @Transactional
    public List<BugResponse> getUploadedBugsByCompany(Company company) {

        return bugRepository.findByPublisher(company).stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BugResponse> getUploadedBugsByCompanyIdentifier(String identifier) {
        User user = getCompanyByEmailOrUsername(identifier);

        if (!(user instanceof Company)) {
            return Collections.emptyList();
        }

        Company company = (Company) user;
        return getUploadedBugsByCompany(company);
    }

    @Transactional(readOnly = true)
    public User getCompanyByEmailOrUsername(String identifier) {
        Optional<User> userByEmail = userRepository.findByEmail(identifier);
        if (userByEmail.isPresent()) {
            return userByEmail.get();
        }
        Optional<Company> companyByName = companyRepository.findByCompanyName(identifier);
        if (companyByName.isPresent()) {
            return companyByName.get();
        }

        throw new ResourceNotFoundException("User not found with identifier: " + identifier);
    }

    public Long getClaimerId(Long id) {
        Bug bug = bugRepository.findById(id).orElseThrow(() -> new BadRequestException(String.format("No bug with id %d", id)));
        List<BugClaim> claims = bug.getBugClaims();
        if (claims.isEmpty()) return -1L;
        else return claims.get(0).getDeveloper().getId();
    }
}
