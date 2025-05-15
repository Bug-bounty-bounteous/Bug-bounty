package com.bounteous.bug_bounty_backend.controllers;

import com.bounteous.bug_bounty_backend.data.dto.requests.feedback.FeedbackRequest;
import com.bounteous.bug_bounty_backend.data.dto.responses.ApiResponse;
import com.bounteous.bug_bounty_backend.data.dto.responses.feedback.FeedbackResponse;
import com.bounteous.bug_bounty_backend.security.JwtTokenProvider;
import com.bounteous.bug_bounty_backend.services.FeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Manages feedback endpoints
@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedbackController {
    private final FeedbackService feedbackService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/submit")
    public ResponseEntity<ApiResponse> submitFeedback(
            @RequestBody @Valid FeedbackRequest request,
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.substring(7);
        String email = jwtTokenProvider.getEmailFromToken(token);
        feedbackService.submitFeedback(request, email);
        return ResponseEntity.ok(new ApiResponse(true, "Feedback submitted successfully"));
    }

    @GetMapping("/solution/{solutionId}")
    public ResponseEntity<List<FeedbackResponse>> getFeedbackForSolution(@PathVariable Long solutionId) {
        List<FeedbackResponse> feedbacks = feedbackService.getFeedbackBySolutionId(solutionId);
        return ResponseEntity.ok(feedbacks);
    }

}
