package com.bounteous.bug_bounty_backend.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bounteous.bug_bounty_backend.data.dto.responses.leaderboard.LeaderboardResponse;
import com.bounteous.bug_bounty_backend.data.dto.responses.leaderboard.LeaderboardResponse.DeveloperInfo;
import com.bounteous.bug_bounty_backend.data.entities.humans.Developer;
import com.bounteous.bug_bounty_backend.data.entities.humans.User;
import com.bounteous.bug_bounty_backend.data.repositories.humans.DeveloperRepository;

import lombok.RequiredArgsConstructor;

// Handles leaderboard-related business logic
@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final DeveloperRepository developerRepository;
    private final UserService userService;

    // Service methods will be added here
    @Transactional(readOnly = true)
    public LeaderboardResponse getLeaderBoardInfo(int number, User me) {
        // Get top x devs
        List<Developer> topDevs = developerRepository.findTopDevelopers(PageRequest.of(0, number));
        List<DeveloperInfo> devsDtos = topDevs.stream().map(d -> new DeveloperInfo(d.getId(), d.getFirstName(), d.getLastName(), d.getEmail(), d.getPoints(), d.getRating(), userService.getClaimedBugsByDeveloper(d))
        ).collect(Collectors.toList());
        // Get my rank
        boolean amIDev = isDeveloper(me.getId());
        int myRank = (amIDev) ? getDeveloperRank(me.getId()) : -1;
        float myRating = (amIDev) ? developerRepository.findById(me.getId()).get().getRating() : -1f;
        int myPoints = (amIDev) ? developerRepository.findById(me.getId()).get().getPoints() : -1;
        return new LeaderboardResponse(devsDtos, myRank, myRating, myPoints, devsDtos.size());
    }

    @Transactional(readOnly = true)
    public int getDeveloperRank(Long developerId) {
        List<Developer> orderedDevs = developerRepository.findTopDevelopers(Pageable.unpaged());
        for (int i = 0; i < orderedDevs.size(); i++) {
            if (orderedDevs.get(i).getId().equals(developerId)) {
                return i + 1; // +1 because index starts at 0
            }
        }
        return -1; // Not found
    }

    private boolean isDeveloper(Long id) {
        return developerRepository.existsById(id);
    }
}
