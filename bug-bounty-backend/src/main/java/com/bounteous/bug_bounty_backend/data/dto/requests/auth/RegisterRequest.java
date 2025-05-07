package com.bounteous.bug_bounty_backend.data.dto.requests.auth;

import lombok.Data;

// DTO for registration requests
@Data
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private String userType; // "DEVELOPER" or "COMPANY"
    private String companyName; // Only for companies
    private String username; // Only for developers
}
