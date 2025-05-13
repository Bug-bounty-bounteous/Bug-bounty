package com.bounteous.bug_bounty_backend.controllers;

import com.bounteous.bug_bounty_backend.data.dto.responses.solution.SolutionResponse;
import com.bounteous.bug_bounty_backend.data.entities.bugs.Solution;
import com.bounteous.bug_bounty_backend.data.dto.requests.solution.SolutionRequest;
import com.bounteous.bug_bounty_backend.services.SolutionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/solutions")
@RequiredArgsConstructor
public class SolutionController {

    private final SolutionService solutionService;

    @GetMapping("/developer/{developerId}")
    public ResponseEntity<List<SolutionResponse>> getSolutionsByDeveloper(@PathVariable Long developerId) {
        List<SolutionResponse> solutions = solutionService.getSolutionsByDeveloperId(developerId);
        return ResponseEntity.ok(solutions);
    }

    @PostMapping
    public ResponseEntity<Long> postSolution(
            @RequestBody @Valid SolutionRequest request,
            Authentication authentication) throws Exception {
        String username = authentication.getName();
        return ResponseEntity.status(HttpStatus.OK).body(solutionService.postSolution(request, username));
    }
}
