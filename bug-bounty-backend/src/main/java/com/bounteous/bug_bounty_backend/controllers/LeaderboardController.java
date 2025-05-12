package com.bounteous.bug_bounty_backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bounteous.bug_bounty_backend.data.dto.responses.leaderboard.LeaderboardResponse;
import com.bounteous.bug_bounty_backend.data.entities.humans.User;
import com.bounteous.bug_bounty_backend.services.LeaderboardService;
import com.bounteous.bug_bounty_backend.services.UserService;

import lombok.RequiredArgsConstructor;

// Manages leaderboard endpoints
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/leaderboard")
@CrossOrigin(origins = "*")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;
    private final UserService userService;
    // Controller methods will be added here

    // Get leaderboard info (top 10 dev + me)
    @GetMapping("/{top}")
    public ResponseEntity<LeaderboardResponse> getTopDevelopers(@PathVariable int top, Authentication authentication) {
        String identifier = authentication.getName();
        User me = userService.getUserByEmailOrUsername(identifier);
        return ResponseEntity.ok(leaderboardService.getLeaderBoardInfo(top, me));
    }

}
