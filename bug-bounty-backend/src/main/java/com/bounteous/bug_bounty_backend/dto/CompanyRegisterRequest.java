package com.bounteous.bug_bounty_backend.dto;

import lombok.Data;

@Data
public class CompanyRegisterRequest {
    private String companyName;
    private String email;
    private String plainPassword;
}
