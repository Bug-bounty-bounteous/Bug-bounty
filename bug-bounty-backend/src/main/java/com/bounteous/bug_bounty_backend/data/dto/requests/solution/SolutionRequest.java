package com.bounteous.bug_bounty_backend.data.dto.requests.solution;

import lombok.Data;

// DTO for solution submission requests
@Data
public class SolutionRequest {
    private Long bugId;
    private String description;
    private String codeLink;
}
