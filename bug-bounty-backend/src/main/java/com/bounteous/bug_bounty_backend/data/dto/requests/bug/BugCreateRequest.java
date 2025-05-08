package com.bounteous.bug_bounty_backend.data.dto.requests.bug;

import lombok.Data;
import java.util.List;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
public class BugCreateRequest {
    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 100, message = "Title must be between 5 and 100 characters")
    private String title;
    
    @NotBlank(message = "Description is required")
    @Size(min = 20, message = "Description must be at least 20 characters")
    private String description;
    
    @NotBlank(message = "Difficulty is required")
    private String difficulty;
    
    @NotNull(message = "Reward is required")
    @Min(value = 1, message = "Reward must be greater than 0")
    private Double reward;
    
    private List<Long> techStackIds;
}