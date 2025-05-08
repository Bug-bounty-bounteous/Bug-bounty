package com.bounteous.bug_bounty_backend.dto;

import lombok.Data;

@Data
public class DeveloperRegisterRequest {
    private String name;
    private String email;
    private String plainPassword;
    private String githubUsername;
    private String bio;
}

