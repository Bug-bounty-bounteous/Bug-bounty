package com.bounteous.bug_bounty_backend.dto;

import lombok.Data;

@Data
public class CompanyLoginRequest {
    private String email;
    private String plainPassword;
}
