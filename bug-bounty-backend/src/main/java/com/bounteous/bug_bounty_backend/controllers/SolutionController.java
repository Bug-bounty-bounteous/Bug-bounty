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
        String email = authentication.getName();
        return ResponseEntity.status(HttpStatus.OK).body(solutionService.postSolution(request, email));
    }
    @GetMapping("/bug/{bugId}")
    public ResponseEntity<List<SolutionResponse>> getSolutionsByBug(@PathVariable Long bugId) {
        List<SolutionResponse> solutions = solutionService.getSolutionsByBugId(bugId);
        return ResponseEntity.ok(solutions);
    }
    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<SolutionResponse>> getSolutionsByCompany(@PathVariable Long companyId) {
        List<SolutionResponse> solutions = solutionService.getSolutionsByCompanyId(companyId);
        return ResponseEntity.ok(solutions);
    }


}
