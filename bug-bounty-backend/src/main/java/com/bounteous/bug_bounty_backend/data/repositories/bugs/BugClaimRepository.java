package com.bounteous.bug_bounty_backend.data.repositories.bugs;

import com.bounteous.bug_bounty_backend.data.entities.bugs.BugClaim;
import com.bounteous.bug_bounty_backend.data.entities.bugs.BugClaimId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BugClaimRepository extends JpaRepository<BugClaim, BugClaimId> {
}
