package com.bounteous.bug_bounty_backend.controllers;

import com.bounteous.bug_bounty_backend.data.dto.requests.resource.ResourceRequest;
import com.bounteous.bug_bounty_backend.data.dto.responses.ApiResponse;
import com.bounteous.bug_bounty_backend.data.entities.others.LearningResource;
import com.bounteous.bug_bounty_backend.data.entities.others.ResourceType;
import com.bounteous.bug_bounty_backend.services.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ResourceController {
    
    private final ResourceService resourceService;
    
    /**
     * Get all learning resources with optional filtering
     */
    @GetMapping
    public ResponseEntity<Page<LearningResource>> getResources(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String query,
            Pageable pageable
    ) {
        return ResponseEntity.ok(resourceService.getResources(type, query, pageable));
    }
    
    /**
     * Get a specific resource by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<LearningResource> getResourceById(@PathVariable Long id) {
        return ResponseEntity.ok(resourceService.getResourceById(id));
    }
    
    /**
     * Get all available resource types
     */
    @GetMapping("/types")
    public ResponseEntity<List<String>> getResourceTypes() {
        return ResponseEntity.ok(Arrays.stream(ResourceType.values())
                .map(Enum::name)
                .collect(Collectors.toList()));
    }
    
    /**
     * Create a new learning resource (companies only)
     */
    @PostMapping
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<LearningResource> createResource(
            @RequestBody @Valid ResourceRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName();
        return ResponseEntity.ok(resourceService.createResource(request, email));
    }
    
    /**
     * Report a resource as outdated or broken
     */
    @PostMapping("/{id}/report")
    public ResponseEntity<ApiResponse> reportResource(
            @PathVariable Long id,
            Authentication authentication
    ) {
        resourceService.reportResource(id);
        return ResponseEntity.ok(new ApiResponse(true, "Resource reported successfully"));
    }
    
    /**
     * Update a learning resource (companies can only update their own)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<LearningResource> updateResource(
            @PathVariable Long id,
            @RequestBody @Valid ResourceRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName();
        return ResponseEntity.ok(resourceService.updateResource(id, request, email));
    }
    
    /**
     * Get reported resources (for admin review)
     */
    @GetMapping("/reported")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<LearningResource>> getReportedResources() {
        return ResponseEntity.ok(resourceService.getReportedResources());
    }
    
    /**
     * Clear report flag on a resource
     */
    @PostMapping("/{id}/clear-report")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> clearResourceReport(@PathVariable Long id) {
        resourceService.clearResourceReport(id);
        return ResponseEntity.ok(new ApiResponse(true, "Report cleared successfully"));
    }
    
    /**
     * Get resources by company
     */
    @GetMapping("/company")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<List<LearningResource>> getResourcesByCompany(
            Authentication authentication
    ) {
        String email = authentication.getName();
        return ResponseEntity.ok(resourceService.getResourcesByCompany(email));
    }
}