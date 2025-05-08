package com.bounteous.bug_bounty_backend.data.repositories.humans;

import com.bounteous.bug_bounty_backend.data.entities.humans.Developer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeveloperRepository extends JpaRepository<Developer, Long> {
    Optional<Developer> findByEmail(String email);
}
