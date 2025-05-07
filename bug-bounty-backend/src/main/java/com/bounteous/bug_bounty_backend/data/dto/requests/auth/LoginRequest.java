package com.bounteous.bug_bounty_backend.data.dto.requests.auth;

import lombok.Data;

// DTO for login requests
@Data
public class LoginRequest {
    private String email;
    private String password;
}
