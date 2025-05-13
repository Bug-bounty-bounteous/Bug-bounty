package com.bounteous.bug_bounty_backend.data.dto.responses.feedback;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackResponse {
    private Long id;
    private String feedbackMessage;
    private int rating;
    private LocalDateTime submittedAt;

    private CompanyInfo company;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompanyInfo {
        private Long id;
        private String companyName;
        private String email;
    }
}