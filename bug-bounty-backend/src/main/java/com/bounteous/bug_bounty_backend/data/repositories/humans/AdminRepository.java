package com.bounteous.bug_bounty_backend.data.repositories.humans;

import com.bounteous.bug_bounty_backend.data.entities.humans.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Repository for Admin entities
@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    // Repository methods will be added here
}
