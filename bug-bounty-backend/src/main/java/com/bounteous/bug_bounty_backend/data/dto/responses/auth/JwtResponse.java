package com.bounteous.bug_bounty_backend.data.dto.responses.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

// DTO for JWT authentication response
@Data
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String type;
    private Long id;
    private String email;
    private String userType;
}
