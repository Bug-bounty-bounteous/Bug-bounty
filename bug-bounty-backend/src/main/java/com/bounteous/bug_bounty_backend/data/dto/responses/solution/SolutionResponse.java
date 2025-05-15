package com.bounteous.bug_bounty_backend.data.dto.responses.solution;

import com.bounteous.bug_bounty_backend.data.dto.responses.feedback.FeedbackResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class SolutionResponse {
    private Long id;
    private String description;
    private String codeLink;
    private String status;
    private LocalDateTime submittedAt;
    private LocalDateTime reviewedAt;

    private DeveloperInfo developer;
    private BugInfo bug;

    private List<FeedbackResponse> feedbacks;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeveloperInfo {
        private Long id;
        private String username;
        private String email;
        private Float rating;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BugInfo {
        private Long id;
        private String title;
    }

}
