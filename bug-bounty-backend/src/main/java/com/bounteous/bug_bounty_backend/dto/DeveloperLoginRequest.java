package com.bounteous.bug_bounty_backend.dto;

import lombok.Data;

@Data
public class DeveloperLoginRequest {
    private String email;
    private String plainPassword;
}
