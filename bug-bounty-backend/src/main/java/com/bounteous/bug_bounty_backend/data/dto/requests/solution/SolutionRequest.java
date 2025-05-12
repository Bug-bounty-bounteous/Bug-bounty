package com.bounteous.bug_bounty_backend.data.dto.requests.solution;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

// DTO for solution submission requests
@Data
public class SolutionRequest {

    @NotNull(message="BugId is required")
    private Long bugId;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "codeLink is required")
    private String codeLink;
}
