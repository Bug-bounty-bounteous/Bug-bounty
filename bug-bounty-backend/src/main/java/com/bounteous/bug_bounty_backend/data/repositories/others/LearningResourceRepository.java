package com.bounteous.bug_bounty_backend.data.repositories.others;

import com.bounteous.bug_bounty_backend.data.entities.humans.Company;
import com.bounteous.bug_bounty_backend.data.entities.others.LearningResource;
import com.bounteous.bug_bounty_backend.data.entities.others.ResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LearningResourceRepository extends JpaRepository<LearningResource, Long>, 
        JpaSpecificationExecutor<LearningResource> {
    
    /**
     * Find resources by publisher ordered by creation date
     */
    List<LearningResource> findByPublisherOrderByDateDesc(Company publisher);
    
    /**
     * Find resources by type
     */
    Page<LearningResource> findByResourceType(ResourceType resourceType, Pageable pageable);
    
    /**
     * Find resources by title or description containing text (case-insensitive)
     */
    @Query("SELECT r FROM LearningResource r WHERE " +
           "LOWER(r.title) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(r.description) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    Page<LearningResource> findByTitleOrDescriptionContainingIgnoreCase(
            @Param("searchText") String searchText, Pageable pageable);
    
    /**
     * Find all reported resources
     */
    List<LearningResource> findByReportedTrueOrderByDateDesc();
    
    /**
     * Count resources by publisher
     */
    long countByPublisher(Company publisher);
}