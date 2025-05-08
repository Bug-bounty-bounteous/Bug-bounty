package com.bounteous.bug_bounty_backend.services;

import com.bounteous.bug_bounty_backend.data.dto.responses.bug.BugResponse;
import com.bounteous.bug_bounty_backend.data.entities.bugs.Bug;
import com.bounteous.bug_bounty_backend.data.entities.bugs.BugStatus;
import com.bounteous.bug_bounty_backend.data.entities.bugs.Difficulty;
import com.bounteous.bug_bounty_backend.data.entities.bugs.TechStack;
import com.bounteous.bug_bounty_backend.data.repositories.bugs.BugRepository;
import com.bounteous.bug_bounty_backend.data.repositories.bugs.TechStackRepository;
import com.bounteous.bug_bounty_backend.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BugService {
    
    private final BugRepository bugRepository;
    private final TechStackRepository techStackRepository;
    
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
                spec = spec.and((root, criteriaQuery, cb) -> 
                    cb.equal(root.get("difficulty"), difficulty));
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
                spec = spec.and((root, criteriaQuery, cb) -> 
                    cb.equal(root.get("bugStatus"), status));
            } catch (IllegalArgumentException e) {
                // Invalid status, ignore this filter
            }
        }
        
        // Search in title or description if query is provided
        if (query != null && !query.isEmpty()) {
            String searchTerm = "%" + query.toLowerCase() + "%";
            spec = spec.and((root, criteriaQuery, cb) -> 
                cb.or(
                    cb.like(cb.lower(root.get("title")), searchTerm),
                    cb.like(cb.lower(root.get("description")), searchTerm)
                ));
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
                        techStack.getCategory()
                ))
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
                        techStack.getCategory()
                ))
                .collect(Collectors.toList());
        
        BugResponse.UserInfo publisher = null;
        if (bug.getPublisher() != null) {
            publisher = new BugResponse.UserInfo(
                    bug.getPublisher().getId(),
                    bug.getPublisher().getFirstName() + " " + bug.getPublisher().getLastName(),
                    bug.getPublisher().getCompanyName()
            );
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
                bug.getVerificationStatus() != null ? bug.getVerificationStatus().name() : null
        );
    }
}