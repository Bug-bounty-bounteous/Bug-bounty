package com.bounteous.bug_bounty_backend.dto;

import lombok.Data;

@Data
public class CaptchaUnlockRequest {
    private String email;
    private String captchaCode;
}
