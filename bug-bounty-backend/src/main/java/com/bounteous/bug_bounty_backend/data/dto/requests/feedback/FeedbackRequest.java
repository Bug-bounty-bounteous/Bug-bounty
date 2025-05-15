package com.bounteous.bug_bounty_backend.data.dto.requests.feedback;

import lombok.Data;
import lombok.Builder;

// DTO for feedback submission requests
@Data
@Builder
public class FeedbackRequest {
    private Long solutionId;
    private int rating;
    private String feedbackMessage;
}
