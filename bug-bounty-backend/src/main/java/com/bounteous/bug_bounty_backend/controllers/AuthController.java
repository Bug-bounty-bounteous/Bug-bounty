package com.bounteous.bug_bounty_backend.controllers;

import com.bounteous.bug_bounty_backend.dto.*;
import com.bounteous.bug_bounty_backend.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.bounteous.bug_bounty_backend.data.entities.humans.Company;
import com.bounteous.bug_bounty_backend.data.entities.humans.Developer;
import com.bounteous.bug_bounty_backend.data.repositories.humans.CompanyRepository;
import com.bounteous.bug_bounty_backend.data.repositories.humans.DeveloperRepository;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")

public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register/dev")
    public ResponseEntity<Developer> registerDeveloper(@RequestBody DeveloperRegisterRequest request) {
        Developer developer = authService.registerDeveloper(request);
        return ResponseEntity.ok(developer);
    }
    @PostMapping("/login/dev")
    public ResponseEntity<?> loginDeveloper(@RequestBody DeveloperLoginRequest request) {
        try {
            return ResponseEntity.ok(authService.loginDeveloper(request));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }


    @PostMapping("/register/company")
    public ResponseEntity<Company> registerCompany(@RequestBody CompanyRegisterRequest request) {
        try {
            Company company = authService.registerCompany(request);
            return ResponseEntity.ok(company);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }

    @PostMapping("/login/company")
    public ResponseEntity<?> loginCompany(@RequestBody CompanyLoginRequest request) {
        try {
            Company company = authService.loginCompany(request);
            return ResponseEntity.ok(company);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
    @Autowired
    private DeveloperRepository developerRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @PostMapping("/unlock")
    public ResponseEntity<String> unlockAccount(@RequestBody CaptchaUnlockRequest request) {
        // Try developer
        Optional<Developer> devOpt = developerRepository.findByEmail(request.getEmail());
        if (devOpt.isPresent()) {
            return ResponseEntity.ok(authService.unlockDeveloper(request));
        }

        // Try company
        Optional<Company> compOpt = companyRepository.findByEmail(request.getEmail());
        if (compOpt.isPresent()) {
            return ResponseEntity.ok(authService.unlockCompany(request));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found.");
    }








}
