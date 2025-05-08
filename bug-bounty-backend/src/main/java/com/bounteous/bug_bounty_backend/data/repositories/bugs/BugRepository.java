package com.bounteous.bug_bounty_backend.data.repositories.bugs;

import com.bounteous.bug_bounty_backend.data.entities.bugs.Bug;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BugRepository extends JpaRepository<Bug, Long> {
    Optional<Bug> findByDescription(String description);
}
