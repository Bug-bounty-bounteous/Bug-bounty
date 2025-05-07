package com.bounteous.bug_bounty_backend.data.repositories.bugs;

import com.bounteous.bug_bounty_backend.data.entities.bugs.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Repository for Feedback entities
@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    // Repository methods will be added here
}
