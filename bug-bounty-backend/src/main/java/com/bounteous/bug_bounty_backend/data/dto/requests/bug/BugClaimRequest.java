package com.bounteous.bug_bounty_backend.data.dto.requests.bug;

import lombok.Data;

// DTO for bug claim requests
@Data
public class BugClaimRequest {
    private Long bugId;
    private String claimNote;
}
