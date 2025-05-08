package com.bounteous.bug_bounty_backend.data.repositories.others;

import com.bounteous.bug_bounty_backend.data.entities.others.LearningResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LearningResourceRepository extends JpaRepository<LearningResource, Long> {
}
