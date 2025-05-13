package com.bounteous.bug_bounty_backend.controllers;

import com.bounteous.bug_bounty_backend.data.dto.responses.ApiResponse;
import com.bounteous.bug_bounty_backend.data.dto.responses.bug.BugResponse;
import com.bounteous.bug_bounty_backend.data.entities.bugs.Difficulty;
import com.bounteous.bug_bounty_backend.services.BugClaimService;
import com.bounteous.bug_bounty_backend.services.BugService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bounteous.bug_bounty_backend.data.dto.requests.bug.BugClaimRequest;
import com.bounteous.bug_bounty_backend.data.dto.requests.bug.BugCreateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/bugs")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BugController {
    private final BugService bugService;
    private final BugClaimService bugClaimService;

    /**
     * Get all bugs with optional filtering
     * 
     * @param difficulty   Filter by difficulty level
     * @param techStackIds Filter by technology stack IDs (comma-separated)
     * @param status       Filter by bug status
     * @param query        Search query for title or description
     * @param pageable     Pagination information
     * @return List of bugs matching criteria
     */
    @GetMapping
    public ResponseEntity<Page<BugResponse>> getAllBugs(
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) List<Long> techStackIds,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String query,
            Pageable pageable) {
        return ResponseEntity.ok(bugService.findBugs(difficulty, techStackIds, status, query, pageable));
    }

    /**
     * Get a specific bug by ID
     * 
     * @param id Bug ID
     * @return Bug details
     */
    @GetMapping("/{id}")
    public ResponseEntity<BugResponse> getBugById(@PathVariable Long id) {
        return ResponseEntity.ok(bugService.getBugById(id));
    }

    /**
     * Get all available difficulty levels
     * 
     * @return List of difficulty levels
     */
    @GetMapping("/difficulties")
    public ResponseEntity<List<String>> getAllDifficulties() {
        return ResponseEntity.ok(bugService.getAllDifficulties());
    }

    /**
     * Get all available tech stacks
     * 
     * @return List of tech stacks
     */
    @GetMapping("/tech-stacks")
    public ResponseEntity<List<BugResponse.TechStackInfo>> getAllTechStacks() {
        return ResponseEntity.ok(bugService.getAllTechStacks());
    }

    /**
     * Create a new bug
     * 
     * @param request        The bug creation request data
     * @param authentication The authenticated user
     * @return The created bug
     */
    @PostMapping
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<BugResponse> createBug(
            @RequestBody @Valid BugCreateRequest request,
            Authentication authentication) {

        String email = authentication.getName();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bugService.createBug(request, email));
    }

    @PostMapping("/{id}/claim")
    @PreAuthorize("hasRole('DEVELOPER')")
    public ResponseEntity<ApiResponse> claimBug(
            @PathVariable Long id,
            @RequestBody(required = false) BugClaimRequest request,
            Authentication authentication) {
    
        String identifier = authentication.getName();
        bugClaimService.claimBug(id, identifier, request != null ? request.getClaimNote() : null);
    
        return ResponseEntity.ok(new ApiResponse(true, "Bug claimed successfully"));
    }

    @DeleteMapping("/{id}/claim")
    @PreAuthorize("hasRole('DEVELOPER')")
    public ResponseEntity<ApiResponse> unclaimBug(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String identifier = authentication.getName();
        bugClaimService.unclaimBug(id, identifier);
        return ResponseEntity.ok(new ApiResponse(true, "Bug unclaimed successfully"));
    }
}