package com.bounteous.bug_bounty_backend.data.dto.requests.bug;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BugClaimRequest {
    @NotNull(message = "Bug ID is required")
    private Long bugId;
    
    private String claimNote;
}