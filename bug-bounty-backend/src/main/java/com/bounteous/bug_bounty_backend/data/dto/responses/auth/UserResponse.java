package com.bounteous.bug_bounty_backend.data.dto.responses.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO for user data responses
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private String userType;
    private String username;  // For developers
    private String companyName;  // For companies
    private Float rating;  // For developers
    private Integer points;  // For developers
}
