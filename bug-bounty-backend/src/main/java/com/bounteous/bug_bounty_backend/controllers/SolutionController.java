package com.bounteous.bug_bounty_backend.controllers;

import com.bounteous.bug_bounty_backend.data.dto.requests.bug.BugCreateRequest;
import com.bounteous.bug_bounty_backend.data.dto.requests.solution.SolutionRequest;
import com.bounteous.bug_bounty_backend.services.SolutionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Handles solution-related endpoints
@RestController
@RequestMapping("/api/solutions")
public class SolutionController {
    @Autowired
    private SolutionService solutionService;
    // Controller methods will be added here
    @PostMapping
    public ResponseEntity<Long> postSolution(
            @RequestBody @Valid SolutionRequest request,
            Authentication authentication) throws Exception {
        String username = authentication.getName();
        return ResponseEntity.status(HttpStatus.OK).body(solutionService.postSolution(request, username));
    }
}
