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
import com.bounteous.bug_bounty_backend.services.ResourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {

    @Mock
    private LearningResourceRepository resourceRepository;
    
    @Mock
    private CompanyRepository companyRepository;
    
    @InjectMocks
    private ResourceService resourceService;

    private Company company;
    private Company otherCompany;
    private LearningResource learningResource;
    private ResourceRequest resourceRequest;
    private List<LearningResource> resources;
    private Page<LearningResource> resourcePage;

    @BeforeEach
    void setUp() {
        // Set up companies
        company = Company.builder()
                .id(1L)
                .email("company@example.com")
                .companyName("Example Corp")
                .role("COMPANY")
                .build();

        otherCompany = Company.builder()
                .id(2L)
                .email("other@example.com")
                .companyName("Other Corp")
                .role("COMPANY")
                .build();

        // Set up learning resource
        learningResource = LearningResource.builder()
                .id(1L)
                .title("Spring Boot Tutorial")
                .description("Learn Spring Boot from scratch")
                .url("https://example.com/spring-boot-tutorial")
                .resourceType(ResourceType.TUTORIAL)
                .date(LocalDateTime.now())
                .reported(false)
                .publisher(company)
                .build();

        // Set up resource request
        resourceRequest = ResourceRequest.builder()
                .title("Spring Boot Tutorial")
                .description("Learn Spring Boot from scratch")
                .url("https://example.com/spring-boot-tutorial")
                .resourceType("TUTORIAL")
                .build();

        // Set up resource list and page
        resources = List.of(learningResource);
        resourcePage = new PageImpl<>(resources, PageRequest.of(0, 10), 1);
    }

    // Test access to learning materials (Requirement 13)
    @Test
    void getResources_WithoutFilters_ReturnsAllResources() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        when(resourceRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(resourcePage);

        // When
        Page<LearningResource> result = resourceService.getResources(null, null, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        LearningResource resource = result.getContent().get(0);
        assertEquals(learningResource.getId(), resource.getId());
        assertEquals(learningResource.getTitle(), resource.getTitle());
        assertEquals(learningResource.getDescription(), resource.getDescription());
        assertEquals(learningResource.getUrl(), resource.getUrl());
        assertEquals(learningResource.getResourceType(), resource.getResourceType());
        
        verify(resourceRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getResources_WithTypeFilter_ReturnsFilteredResources() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        when(resourceRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(resourcePage);

        // When
        Page<LearningResource> result = resourceService.getResources("TUTORIAL", null, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(resourceRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getResources_WithInvalidType_IgnoresFilter() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        when(resourceRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(resourcePage);

        // When
        Page<LearningResource> result = resourceService.getResources("INVALID_TYPE", null, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(resourceRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getResources_WithSearchQuery_ReturnsFilteredResources() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        when(resourceRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(resourcePage);

        // When
        Page<LearningResource> result = resourceService.getResources(null, "Spring", pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(resourceRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getResourceById_ExistingResource_ReturnsResource() {
        // Given
        when(resourceRepository.findById(1L)).thenReturn(Optional.of(learningResource));

        // When
        LearningResource result = resourceService.getResourceById(1L);

        // Then
        assertNotNull(result);
        assertEquals(learningResource.getId(), result.getId());
        assertEquals(learningResource.getTitle(), result.getTitle());
        assertEquals(learningResource.getDescription(), result.getDescription());
        assertEquals(learningResource.getUrl(), result.getUrl());
        
        verify(resourceRepository).findById(1L);
    }

    @Test
    void getResourceById_NonExistingResource_ThrowsException() {
        // Given
        when(resourceRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> resourceService.getResourceById(1L));
        
        assertEquals("Learning resource not found with id: 1", exception.getMessage());
        verify(resourceRepository).findById(1L);
    }

    // Test company provided resources (Requirement 14)
    @Test
    void createResource_ValidRequest_CreatesResource() {
        // Given
        when(companyRepository.findByEmail(company.getEmail())).thenReturn(Optional.of(company));
        when(resourceRepository.save(any(LearningResource.class))).thenReturn(learningResource);

        // When
        LearningResource result = resourceService.createResource(resourceRequest, company.getEmail());

        // Then
        assertNotNull(result);
        assertEquals(learningResource.getTitle(), result.getTitle());
        assertEquals(learningResource.getDescription(), result.getDescription());
        assertEquals(learningResource.getUrl(), result.getUrl());
        assertEquals(learningResource.getResourceType(), result.getResourceType());
        assertEquals(learningResource.getPublisher(), result.getPublisher());
        
        verify(companyRepository).findByEmail(company.getEmail());
        verify(resourceRepository).save(any(LearningResource.class));
    }

    @Test
    void createResource_CompanyNotFound_ThrowsException() {
        // Given
        when(companyRepository.findByEmail(company.getEmail())).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> resourceService.createResource(resourceRequest, company.getEmail()));
        
        assertEquals("Company not found", exception.getMessage());
        verify(companyRepository).findByEmail(company.getEmail());
        verify(resourceRepository, never()).save(any());
    }

    @Test
    void createResource_InvalidResourceType_ThrowsException() {
        // Given
        resourceRequest.setResourceType("INVALID_TYPE");
        when(companyRepository.findByEmail(company.getEmail())).thenReturn(Optional.of(company));

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class,
            () -> resourceService.createResource(resourceRequest, company.getEmail()));
        
        assertEquals("Invalid resource type: INVALID_TYPE", exception.getMessage());
        verify(companyRepository).findByEmail(company.getEmail());
        verify(resourceRepository, never()).save(any());
    }

    @Test
    void createResource_InvalidUrl_ThrowsException() {
        // Given
        resourceRequest.setUrl("invalid-url");
        when(companyRepository.findByEmail(company.getEmail())).thenReturn(Optional.of(company));

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class,
            () -> resourceService.createResource(resourceRequest, company.getEmail()));
        
        assertEquals("Invalid URL format", exception.getMessage());
        verify(companyRepository).findByEmail(company.getEmail());
        verify(resourceRepository, never()).save(any());
    }

    @Test
    void createResource_WithAllResourceTypes_Success() {
        // Given
        when(companyRepository.findByEmail(company.getEmail())).thenReturn(Optional.of(company));
        when(resourceRepository.save(any(LearningResource.class))).thenReturn(learningResource);

        // Test all resource types
        String[] resourceTypes = {"TUTORIAL", "DOCUMENTATION", "VIDEO", "ARTICLE", "CODE_EXAMPLE"};
        
        for (String type : resourceTypes) {
            // When
            resourceRequest.setResourceType(type);
            LearningResource result = resourceService.createResource(resourceRequest, company.getEmail());
            
            // Then
            assertNotNull(result);
        }
        
        verify(resourceRepository, times(5)).save(any(LearningResource.class));
    }

    @Test
    void reportResource_ValidResource_MarksAsReported() {
        // Given
        when(resourceRepository.findById(1L)).thenReturn(Optional.of(learningResource));
        when(resourceRepository.save(learningResource)).thenReturn(learningResource);

        // When
        resourceService.reportResource(1L);

        // Then
        assertTrue(learningResource.isReported());
        verify(resourceRepository).findById(1L);
        verify(resourceRepository).save(learningResource);
    }

    @Test
    void reportResource_NonExistingResource_ThrowsException() {
        // Given
        when(resourceRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> resourceService.reportResource(1L));
        
        assertEquals("Learning resource not found with id: 1", exception.getMessage());
        verify(resourceRepository).findById(1L);
        verify(resourceRepository, never()).save(any());
    }

    @Test
    void updateResource_ValidOwner_UpdatesResource() {
        // Given
        when(resourceRepository.findById(1L)).thenReturn(Optional.of(learningResource));
        when(companyRepository.findByEmail(company.getEmail())).thenReturn(Optional.of(company));
        when(resourceRepository.save(learningResource)).thenReturn(learningResource);

        ResourceRequest updateRequest = ResourceRequest.builder()
                .title("Updated Title")
                .description("Updated description")
                .url("https://example.com/updated")
                .resourceType("DOCUMENTATION")
                .build();

        // When
        LearningResource result = resourceService.updateResource(1L, updateRequest, company.getEmail());

        // Then
        assertNotNull(result);
        assertEquals("Updated Title", learningResource.getTitle());
        assertEquals("Updated description", learningResource.getDescription());
        assertEquals("https://example.com/updated", learningResource.getUrl());
        assertEquals(ResourceType.DOCUMENTATION, learningResource.getResourceType());
        
        verify(resourceRepository).findById(1L);
        verify(companyRepository).findByEmail(company.getEmail());
        verify(resourceRepository).save(learningResource);
    }

    @Test
    void updateResource_NotOwner_ThrowsException() {
        // Given
        when(resourceRepository.findById(1L)).thenReturn(Optional.of(learningResource));
        when(companyRepository.findByEmail(otherCompany.getEmail())).thenReturn(Optional.of(otherCompany));

        // When & Then
        ForbiddenException exception = assertThrows(ForbiddenException.class,
            () -> resourceService.updateResource(1L, resourceRequest, otherCompany.getEmail()));
        
        assertEquals("You can only update your own resources", exception.getMessage());
        verify(resourceRepository).findById(1L);
        verify(companyRepository).findByEmail(otherCompany.getEmail());
        verify(resourceRepository, never()).save(any());
    }

    @Test
    void getReportedResources_ReturnsReportedResources() {
        // Given
        learningResource.setReported(true);
        when(resourceRepository.findByReportedTrueOrderByDateDesc()).thenReturn(resources);

        // When
        List<LearningResource> result = resourceService.getReportedResources();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).isReported());
        verify(resourceRepository).findByReportedTrueOrderByDateDesc();
    }

    @Test
    void clearResourceReport_ValidResource_ClearsReport() {
        // Given
        learningResource.setReported(true);
        when(resourceRepository.findById(1L)).thenReturn(Optional.of(learningResource));
        when(resourceRepository.save(learningResource)).thenReturn(learningResource);

        // When
        resourceService.clearResourceReport(1L);

        // Then
        assertFalse(learningResource.isReported());
        verify(resourceRepository).findById(1L);
        verify(resourceRepository).save(learningResource);
    }

    @Test
    void getResourcesByCompany_ValidCompany_ReturnsCompanyResources() {
        // Given
        when(companyRepository.findByEmail(company.getEmail())).thenReturn(Optional.of(company));
        when(resourceRepository.findByPublisherOrderByDateDesc(company)).thenReturn(resources);

        // When
        List<LearningResource> result = resourceService.getResourcesByCompany(company.getEmail());

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(learningResource.getId(), result.get(0).getId());
        verify(companyRepository).findByEmail(company.getEmail());
        verify(resourceRepository).findByPublisherOrderByDateDesc(company);
    }

    @Test
    void getResourcesByCompany_CompanyNotFound_ThrowsException() {
        // Given
        when(companyRepository.findByEmail(company.getEmail())).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> resourceService.getResourcesByCompany(company.getEmail()));
        
        assertEquals("Company not found", exception.getMessage());
        verify(companyRepository).findByEmail(company.getEmail());
        verify(resourceRepository, never()).findByPublisherOrderByDateDesc(any());
    }

    // Test URL validation
    @Test
    void isValidUrl_ValidHttpUrl_ReturnsTrue() {
        // Given
        ResourceRequest httpRequest = ResourceRequest.builder()
                .title("Test")
                .description("Test description")
                .url("http://example.com")
                .resourceType("TUTORIAL")
                .build();
        when(companyRepository.findByEmail(company.getEmail())).thenReturn(Optional.of(company));
        when(resourceRepository.save(any(LearningResource.class))).thenReturn(learningResource);

        // When & Then
        assertDoesNotThrow(() -> resourceService.createResource(httpRequest, company.getEmail()));
    }

    @Test
    void isValidUrl_ValidHttpsUrl_ReturnsTrue() {
        // Given
        ResourceRequest httpsRequest = ResourceRequest.builder()
                .title("Test")
                .description("Test description")
                .url("https://example.com")
                .resourceType("TUTORIAL")
                .build();
        when(companyRepository.findByEmail(company.getEmail())).thenReturn(Optional.of(company));
        when(resourceRepository.save(any(LearningResource.class))).thenReturn(learningResource);

        // When & Then
        assertDoesNotThrow(() -> resourceService.createResource(httpsRequest, company.getEmail()));
    }

    @Test
    void isValidUrl_NullUrl_ReturnsFalse() {
        // Given
        ResourceRequest nullUrlRequest = ResourceRequest.builder()
                .title("Test")
                .description("Test description")
                .url(null)
                .resourceType("TUTORIAL")
                .build();
        when(companyRepository.findByEmail(company.getEmail())).thenReturn(Optional.of(company));

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class,
            () -> resourceService.createResource(nullUrlRequest, company.getEmail()));
        assertEquals("Invalid URL format", exception.getMessage());
    }

    @Test
    void isValidUrl_EmptyUrl_ReturnsFalse() {
        // Given
        ResourceRequest emptyUrlRequest = ResourceRequest.builder()
                .title("Test")
                .description("Test description")
                .url("")
                .resourceType("TUTORIAL")
                .build();
        when(companyRepository.findByEmail(company.getEmail())).thenReturn(Optional.of(company));

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class,
            () -> resourceService.createResource(emptyUrlRequest, company.getEmail()));
        assertEquals("Invalid URL format", exception.getMessage());
    }
}