package com.bounteous.bug_bounty_backend.services;

import com.bounteous.bug_bounty_backend.data.dto.requests.resource.ResourceRequest;
import com.bounteous.bug_bounty_backend.data.entities.humans.Company;
import com.bounteous.bug_bounty_backend.data.entities.others.LearningResource;
import com.bounteous.bug_bounty_backend.data.entities.others.ResourceType;
import com.bounteous.bug_bounty_backend.data.repositories.humans.CompanyRepository;
import com.bounteous.bug_bounty_backend.data.repositories.others.LearningResourceRepository;
import com.bounteous.bug_bounty_backend.exceptions.BadRequestException;
import com.bounteous.bug_bounty_backend.exceptions.ForbiddenException;
import com.bounteous.bug_bounty_backend.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceService {
    
    private final LearningResourceRepository resourceRepository;
    private final CompanyRepository companyRepository;
    
    /**
     * Get all resources with optional filtering
     */
    @Transactional(readOnly = true)
    public Page<LearningResource> getResources(String type, String query, Pageable pageable) {
        Specification<LearningResource> spec = Specification.where(null);
        
        // Filter by resource type
        if (type != null && !type.isEmpty()) {
            try {
                ResourceType resourceType = ResourceType.valueOf(type.toUpperCase());
                spec = spec.and((root, criteriaQuery, cb) -> 
                    cb.equal(root.get("resourceType"), resourceType));
            } catch (IllegalArgumentException e) {
                // Invalid resource type, ignore filter
            }
        }
        
        // Search in title or description
        if (query != null && !query.isEmpty()) {
            String searchTerm = "%" + query.toLowerCase() + "%";
            spec = spec.and((root, criteriaQuery, cb) -> cb.or(
                    cb.like(cb.lower(root.get("title")), searchTerm),
                    cb.like(cb.lower(root.get("description")), searchTerm)
            ));
        }
        
        return resourceRepository.findAll(spec, pageable);
    }
    
    /**
     * Get a single resource by ID
     */
    @Transactional(readOnly = true)
    public LearningResource getResourceById(Long id) {
        return resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Learning resource not found with id: " + id));
    }
    
    /**
     * Create a new learning resource
     */
    @Transactional
    public LearningResource createResource(ResourceRequest request, String companyEmail) {
        Company company = companyRepository.findByEmail(companyEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
        
        // Validate resource type
        ResourceType resourceType;
        try {
            resourceType = ResourceType.valueOf(request.getResourceType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid resource type: " + request.getResourceType());
        }
        
        // Validate URL format (basic validation)
        if (!isValidUrl(request.getUrl())) {
            throw new BadRequestException("Invalid URL format");
        }
        
        LearningResource resource = LearningResource.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .url(request.getUrl())
                .resourceType(resourceType)
                .date(LocalDateTime.now())
                .reported(false)
                .publisher(company)
                .build();
        
        return resourceRepository.save(resource);
    }
    
    /**
     * Report a resource as outdated or broken
     */
    @Transactional
    public void reportResource(Long id) {
        LearningResource resource = getResourceById(id);
        resource.setReported(true);
        resourceRepository.save(resource);
    }
    
    /**
     * Update an existing resource (companies can only update their own)
     */
    @Transactional
    public LearningResource updateResource(Long id, ResourceRequest request, String companyEmail) {
        LearningResource resource = getResourceById(id);
        Company company = companyRepository.findByEmail(companyEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
        
        // Check if the company owns this resource
        if (!resource.getPublisher().getId().equals(company.getId())) {
            throw new ForbiddenException("You can only update your own resources");
        }
        
        // Validate resource type if provided
        ResourceType resourceType;
        try {
            resourceType = ResourceType.valueOf(request.getResourceType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid resource type: " + request.getResourceType());
        }
        
        // Validate URL format
        if (!isValidUrl(request.getUrl())) {
            throw new BadRequestException("Invalid URL format");
        }
        
        // Update the resource
        resource.setTitle(request.getTitle());
        resource.setDescription(request.getDescription());
        resource.setUrl(request.getUrl());
        resource.setResourceType(resourceType);
        
        return resourceRepository.save(resource);
    }
    
    /**
     * Get all reported resources
     */
    @Transactional(readOnly = true)
    public List<LearningResource> getReportedResources() {
        return resourceRepository.findByReportedTrueOrderByDateDesc();
    }
    
    /**
     * Clear report flag on a resource
     */
    @Transactional
    public void clearResourceReport(Long id) {
        LearningResource resource = getResourceById(id);
        resource.setReported(false);
        resourceRepository.save(resource);
    }
    
    /**
     * Get resources by company
     */
    @Transactional(readOnly = true)
    public List<LearningResource> getResourcesByCompany(String companyEmail) {
        Company company = companyRepository.findByEmail(companyEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
        
        return resourceRepository.findByPublisherOrderByDateDesc(company);
    }
    
    /**
     * Basic URL validation
     */
    private boolean isValidUrl(String url) {
        // Check for null or empty
        if (url == null || url.trim().isEmpty()) {
            return false;
        }
        
        // Check if URL starts with http or https
        String trimmedUrl = url.trim().toLowerCase();
        return trimmedUrl.startsWith("http://") || trimmedUrl.startsWith("https://");
    }
}