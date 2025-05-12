package com.bounteous.bug_bounty_backend.services;

import com.bounteous.bug_bounty_backend.data.dto.responses.auth.UserResponse;
import com.bounteous.bug_bounty_backend.data.dto.responses.bug.BugResponse;
import com.bounteous.bug_bounty_backend.data.entities.bugs.Bug;
import com.bounteous.bug_bounty_backend.data.entities.humans.Company;
import com.bounteous.bug_bounty_backend.data.entities.humans.Developer;
import com.bounteous.bug_bounty_backend.data.entities.humans.User;
import com.bounteous.bug_bounty_backend.data.repositories.bugs.BugClaimRepository;
import com.bounteous.bug_bounty_backend.data.repositories.humans.CompanyRepository;
import com.bounteous.bug_bounty_backend.data.repositories.humans.DeveloperRepository;
import com.bounteous.bug_bounty_backend.data.repositories.humans.UserRepository;
import com.bounteous.bug_bounty_backend.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final DeveloperRepository developerRepository;
    private final CompanyRepository companyRepository;
    private final BugClaimRepository bugClaimRepository;  // Add this repository
    
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }
    
    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }
    
    /**
     * Finds a user by email or username, regardless of user type
     */
    @Transactional(readOnly = true)
    public User getUserByEmailOrUsername(String identifier) {
        Optional<User> userByEmail = userRepository.findByEmail(identifier);
        if (userByEmail.isPresent()) {
            return userByEmail.get();
        }
        
        Optional<Developer> developerByUsername = developerRepository.findByUsername(identifier);
        if (developerByUsername.isPresent()) {
            return developerByUsername.get();
        }
        
        Optional<Company> companyByName = companyRepository.findByCompanyName(identifier);
        if (companyByName.isPresent()) {
            return companyByName.get();
        }
        
        throw new ResourceNotFoundException("User not found with identifier: " + identifier);
    }
    
    @Transactional(readOnly = true)
    public UserResponse getCurrentUserProfile(User user) {
        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
        
        if (user instanceof Developer dev) {
            userResponse.setUsername(dev.getUsername());
            userResponse.setRating(dev.getRating());
            userResponse.setPoints(dev.getPoints());
        }
        
        return userResponse;
    }
    
    /**
     * Get bugs claimed by a user, identified by email or username
     */
    @Transactional(readOnly = true)
    public List<BugResponse> getClaimedBugsByUserIdentifier(String identifier) {
        User user = getUserByEmailOrUsername(identifier);
        
        if (!(user instanceof Developer)) {
            return Collections.emptyList();
        }
        
        Developer developer = (Developer) user;
        return getClaimedBugsByDeveloper(developer);
    }
    
    /**
     * Get bugs claimed by a developer using a new query to avoid lazy loading issues
     */
    @Transactional(readOnly = true)
    public List<BugResponse> getClaimedBugsByDeveloper(Developer developer) {
        // Use a query to fetch the bug claims along with the related bugs
        // to avoid lazy loading issues
        return bugClaimRepository.findByDeveloper(developer).stream()
                .map(claim -> convertBugToResponse(claim.getBug()))
                .collect(Collectors.toList());
    }
    
    /**
     * Convert Bug to BugResponse
     */
    private BugResponse convertBugToResponse(Bug bug) {
        List<BugResponse.TechStackInfo> techStacks = bug.getStack().stream()
                .map(techStack -> new BugResponse.TechStackInfo(
                        techStack.getId(),
                        techStack.getName(),
                        techStack.getCategory()))
                .collect(Collectors.toList());

        BugResponse.UserInfo publisher = null;
        if (bug.getPublisher() != null) {
            publisher = new BugResponse.UserInfo(
                    bug.getPublisher().getId(),
                    bug.getPublisher().getFirstName() + " " + bug.getPublisher().getLastName(),
                    bug.getPublisher().getCompanyName());
        }

        return new BugResponse(
                bug.getId(),
                bug.getTitle(),
                bug.getDescription(),
                bug.getDifficulty().name(),
                bug.getReward(),
                bug.getBugStatus().name(),
                bug.getCreatedAt(),
                bug.getUpdatedAt(),
                publisher,
                techStacks,
                bug.getVerificationStatus() != null ? bug.getVerificationStatus().name() : null);
    }
}