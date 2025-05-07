package com.bounteous.bug_bounty_backend.data.repositories.humans;

import com.bounteous.bug_bounty_backend.data.entities.humans.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Base repository for all user types
@Repository
public interface UserRepository<T extends User> extends JpaRepository<T, Long> {
    // Repository methods will be added here
}
