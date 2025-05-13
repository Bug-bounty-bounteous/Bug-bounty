package com.bounteous.bug_bounty_backend.data.repositories.bugs;

import com.bounteous.bug_bounty_backend.data.entities.bugs.BugClaim;
import com.bounteous.bug_bounty_backend.data.entities.bugs.BugClaimId;
import com.bounteous.bug_bounty_backend.data.entities.humans.Developer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BugClaimRepository extends JpaRepository<BugClaim, BugClaimId> {
    
    @Query("SELECT bc FROM BugClaim bc JOIN FETCH bc.bug WHERE bc.developer = :developer")
    List<BugClaim> findByDeveloper(Developer developer);
}