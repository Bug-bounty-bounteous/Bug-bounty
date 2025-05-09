package com.bounteous.bug_bounty_backend.data.dto.requests.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CaptchaUnlockRequest {
    private String email;
    private String captchaCode;
}
