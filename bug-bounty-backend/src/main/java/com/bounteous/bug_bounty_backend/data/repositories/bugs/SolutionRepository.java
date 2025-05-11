package com.bounteous.bug_bounty_backend.data.repositories.bugs;

import com.bounteous.bug_bounty_backend.data.entities.bugs.Solution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

// Repository for Solution entities
@Repository
public interface SolutionRepository extends JpaRepository<Solution, Long> {
    // Repository methods will be added here
    List<Solution> findByDeveloper_Id(Long developerId);
}
