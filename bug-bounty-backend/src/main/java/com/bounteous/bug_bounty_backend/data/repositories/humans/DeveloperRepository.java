package com.bounteous.bug_bounty_backend.data.repositories.humans;

import com.bounteous.bug_bounty_backend.data.entities.humans.Developer;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeveloperRepository extends JpaRepository<Developer, Long> {

    Optional<Developer> findByUsername(String username);

    Optional<Developer> findByEmail(String email);

    boolean existsByUsername(String username);

    @Query("SELECT d FROM Developer d WHERE d.suspended = false ORDER BY d.rating DESC, d.points DESC")
    List<Developer> findTopDevelopers(Pageable pageable);
}
