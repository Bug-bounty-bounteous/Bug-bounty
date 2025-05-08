package com.bounteous.bug_bounty_backend.data.repositories.bugs;

import com.bounteous.bug_bounty_backend.data.entities.bugs.Solution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolutionRepository extends JpaRepository<Solution, Long> {
}
