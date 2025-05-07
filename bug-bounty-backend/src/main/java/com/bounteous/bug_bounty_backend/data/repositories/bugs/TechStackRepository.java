package com.bounteous.bug_bounty_backend.data.repositories.bugs;

import com.bounteous.bug_bounty_backend.data.entities.bugs.TechStack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Repository for TechStack entities
@Repository
public interface TechStackRepository extends JpaRepository<TechStack, Long> {
    // Repository methods will be added here
}
