package com.bounteous.bug_bounty_backend.repositories;

import com.bounteous.bug_bounty_backend.entities.humans.Developer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeveloperRepository extends JpaRepository<Developer, Long> {
    Optional<Developer> findByEmail(String email);
}
