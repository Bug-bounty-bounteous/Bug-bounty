package com.bounteous.bug_bounty_backend.data.repositories.bugs;

import com.bounteous.bug_bounty_backend.data.entities.bugs.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

// Repository for Feedback entities
@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    // Repository methods will be added here
    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.solution.developer.id = :developerId")
    Double findAverageRatingByDeveloperId(@Param("developerId") Long developerId);
}
