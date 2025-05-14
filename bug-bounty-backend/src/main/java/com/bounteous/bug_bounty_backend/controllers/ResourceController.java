package com.bounteous.bug_bounty_backend.controllers;

import com.bounteous.bug_bounty_backend.data.dto.requests.resource.ResourceRequest;
import com.bounteous.bug_bounty_backend.data.dto.responses.ApiResponse;
import com.bounteous.bug_bounty_backend.data.entities.others.LearningResource;
import com.bounteous.bug_bounty_backend.data.entities.others.ResourceType;
import com.bounteous.bug_bounty_backend.services.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
     * Create a new learning resource with external URL
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<LearningResource> createResource(
            @RequestBody @Valid ResourceRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(resourceService.createResource(request, email));
    }
    
    /**
     * Create a new learning resource with file upload
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<LearningResource> createResourceWithFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("resourceType") String resourceType,
            Authentication authentication
    ) {
        String email = authentication.getName();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(resourceService.createResourceWithFile(file, title, description, resourceType, email));
    }
    
    /**
     * Download a file resource
     */
    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long id) {
        try {
            LearningResource resource = resourceService.getResourceById(id);
            if (!resource.isFileResource()) {
                return ResponseEntity.badRequest().build();
            }
            
            byte[] fileContent = resourceService.downloadFile(resource.getFilePath());
            
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"" + resource.getFileName() + "\"")
                    .body(fileContent);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Update a learning resource
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