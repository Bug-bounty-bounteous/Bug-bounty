package com.bounteous.bug_bounty_backend.data.dto.requests.bug;

import lombok.Data;
import java.util.List;

// DTO for bug creation requests
@Data
public class BugCreateRequest {
    private String title;
    private String description;
    private String difficulty;
    private double reward;
    private List<Long> techStackIds;
}
