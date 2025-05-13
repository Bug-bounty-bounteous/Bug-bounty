package com.bounteous.bug_bounty_backend.data.dto.requests.solution;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.sql.Blob;

import java.sql.Blob;

// DTO for solution submission requests
@Data
public class SolutionRequest {

    @NotNull(message="BugId is required")

    @NotNull(message="BugId is required")
    private Long bugId;

    @NotBlank(message = "Description is required")

    @NotBlank(message = "Description is required")
    private String description;


    private String codeLink;

    private String file;

    private String file;
}