package com.bounteous.bug_bounty_backend.controllers;

import com.bounteous.bug_bounty_backend.data.dto.requests.bug.BugCreateRequest;
import com.bounteous.bug_bounty_backend.data.dto.requests.solution.SolutionRequest;
import com.bounteous.bug_bounty_backend.services.SolutionService;
import jakarta.validation.Valid;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
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

import java.sql.Blob;
import java.sql.SQLException;
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

    @GetMapping("/{solutionId}")
    public ResponseEntity<SolutionResponse> getSolutionById(
            @PathVariable Long solutionId
    ) {
        return ResponseEntity.ok(solutionService.getSolutionById(solutionId));
    }

    @GetMapping("/{solutionId}/file")
    public ResponseEntity<byte[]> getSolutionFile(
            @PathVariable Long solutionId
    ) throws SQLException {
        Pair<byte[], String> solutionFile = solutionService.getSolutionFile(solutionId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + solutionFile.b + "\"")
                .body(solutionFile.a);
    }

    @PostMapping("/{solutionId}/verdict")
    public ResponseEntity<Void> setVerdict(
            @PathVariable Long solutionId,
            @RequestBody @Valid String request,
            Authentication authentication) throws Exception {
        String email = authentication.getName();
        solutionService.setVerdict(solutionId, request, email);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping
    public ResponseEntity<Long> postSolution(
            @RequestBody @Valid SolutionRequest request,
            Authentication authentication) throws Exception {
        String email = authentication.getName();
        return ResponseEntity.status(HttpStatus.OK).body(solutionService.postSolution(request, email));
    }
}
