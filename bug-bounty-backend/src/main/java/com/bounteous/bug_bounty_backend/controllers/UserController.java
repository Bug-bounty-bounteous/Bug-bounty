package com.bounteous.bug_bounty_backend.controllers;

import com.bounteous.bug_bounty_backend.data.dto.responses.auth.UserResponse;
import com.bounteous.bug_bounty_backend.data.dto.responses.bug.BugResponse;
import com.bounteous.bug_bounty_backend.data.entities.humans.User;
import com.bounteous.bug_bounty_backend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        String identifier = authentication.getName();
        User user = userService.getUserByEmailOrUsername(identifier);
        return ResponseEntity.ok(userService.getCurrentUserProfile(user));
    }

    @GetMapping("/me/claimed-bugs")
    public ResponseEntity<List<BugResponse>> getClaimedBugs(Authentication authentication) {
        String identifier = authentication.getName();
        return ResponseEntity.ok(userService.getClaimedBugsByUserIdentifier(identifier));
    }
}