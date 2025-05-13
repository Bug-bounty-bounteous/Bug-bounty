package com.bounteous.bug_bounty_backend.data.dto.requests.feedback;

import lombok.Data;

// DTO for feedback submission requests
@Data
public class FeedbackRequest {
    private Long solutionId;
    private int rating;
    private String feedbackMessage;
}
