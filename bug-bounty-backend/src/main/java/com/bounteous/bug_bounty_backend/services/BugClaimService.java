package com.bounteous.bug_bounty_backend.services;

import com.bounteous.bug_bounty_backend.data.dto.responses.ApiResponse;
import com.bounteous.bug_bounty_backend.data.entities.bugs.Bug;
import com.bounteous.bug_bounty_backend.data.entities.bugs.BugClaim;
import com.bounteous.bug_bounty_backend.data.entities.bugs.BugClaimId;
import com.bounteous.bug_bounty_backend.data.entities.bugs.BugStatus;
import com.bounteous.bug_bounty_backend.data.entities.bugs.ClaimStatus;
import com.bounteous.bug_bounty_backend.data.entities.humans.Developer;
import com.bounteous.bug_bounty_backend.data.entities.humans.User;
import com.bounteous.bug_bounty_backend.data.repositories.bugs.BugClaimRepository;
import com.bounteous.bug_bounty_backend.data.repositories.bugs.BugRepository;
import com.bounteous.bug_bounty_backend.exceptions.BadRequestException;
import com.bounteous.bug_bounty_backend.exceptions.ForbiddenException;
import com.bounteous.bug_bounty_backend.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BugClaimService {
    
    private final BugRepository bugRepository;
    private final UserService userService;
    private final BugClaimRepository bugClaimRepository;
    
    @Transactional
    public ApiResponse claimBug(Long bugId, String userIdentifier, String claimNote) {
        // Find the bug
        Bug bug = bugRepository.findById(bugId)
                .orElseThrow(() -> new ResourceNotFoundException("Bug not found with id: " + bugId));
        
        // Check if the bug is already claimed
        if (bug.getBugStatus() != BugStatus.OPEN) {
            throw new BadRequestException("This bug is already claimed or not available for claiming");
        }
        
        // Find the user by email or username
        User user = userService.getUserByEmailOrUsername(userIdentifier);
        
        // Check if the user is a developer (companies cannot claim bugs)
        if (!(user instanceof Developer)) {
            throw new ForbiddenException("Only developers can claim bugs");
        }
        
        // Check if the developer is trying to claim their own bug
        if (bug.getPublisher() != null && bug.getPublisher().getId().equals(user.getId())) {
            throw new BadRequestException("You cannot claim your own bugs");
        }
        
        Developer developer = (Developer) user;
        
        // Check if the developer has already claimed this bug
        BugClaimId claimId = new BugClaimId(developer.getId(), bugId);
        if (bugClaimRepository.existsById(claimId)) {
            throw new BadRequestException("You have already claimed this bug");
        }
        
        // Create the claim
        BugClaim claim = BugClaim.builder()
                .developer(developer)
                .bug(bug)
                .date(LocalDateTime.now())
                .claimStatus(ClaimStatus.APPROVED) // Auto-approve for now
                .claimNote(claimNote)
                .build();
        
        try {
            // Save the claim
            bugClaimRepository.save(claim);
            
            // Update the bug status
            bug.setBugStatus(BugStatus.CLAIMED);
            bugRepository.save(bug);
            
            return new ApiResponse(true, "Bug claimed successfully");
        } catch (DataIntegrityViolationException e) {
            // Handle race condition - someone else claimed it first
            // This should catch the case where multiple developers try to claim simultaneously
            throw new BadRequestException("This bug was just claimed by another developer");
        }
    }
}