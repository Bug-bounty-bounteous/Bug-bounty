package com.bounteous.bug_bounty_backend.data.dto.responses.bug;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

// DTO for bug data responses
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BugResponse {
    private Long id;
    private String title;
    private String description;
    private String difficulty;
    private double reward;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserInfo publisher;
    private List<TechStackInfo> techStacks;
    private String verificationStatus;
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserInfo {
        private Long id;
        private String name;
        private String companyName;
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TechStackInfo {
        private Long id;
        private String name;
        private String category;
    }
}
