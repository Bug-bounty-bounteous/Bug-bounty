package com.bounteous.bug_bounty_backend.services;

import com.bounteous.bug_bounty_backend.data.entities.humans.Developer;
import com.bounteous.bug_bounty_backend.data.entities.humans.Company;
import com.bounteous.bug_bounty_backend.dto.*;
import com.bounteous.bug_bounty_backend.data.repositories.humans.CompanyRepository;
import com.bounteous.bug_bounty_backend.data.repositories.humans.DeveloperRepository;
import com.bounteous.bug_bounty_backend.utils.PasswordHasher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class AuthService {

    @Autowired
    private DeveloperRepository developerRepository;

    public Developer registerDeveloper(DeveloperRegisterRequest request) {
        if (developerRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email is already registered. Please login or reset your password.");
        }
            Developer dev = new Developer();
        dev.setName(request.getName());
        dev.setEmail(request.getEmail());


        // Hashing logic
        String salt = PasswordHasher.generateSalt(16);
        int iterations = 65536;
        String hashedPassword = PasswordHasher.hashPassword(request.getPlainPassword(), salt, iterations);

        dev.setSalt(salt);
        dev.setPasswordNumIteration(iterations);
        dev.setHashedPassword(hashedPassword);
        dev.setCreatedAt(LocalDate.now());
        dev.setUpdatedAt(LocalDate.now());
        dev.setLoginAttempts(0);
        dev.setAccountLocker(false);

        return developerRepository.save(dev);
    }

//
public Developer loginDeveloper(DeveloperLoginRequest request) {
    Developer dev = developerRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("Developer not found"));

    if (dev.isAccountLocker()) {
        throw new RuntimeException("Account is locked. Please complete CAPTCHA to unlock.");
    }

    System.out.println("Login attempt for: " + request.getEmail());
    System.out.println("Stored hash: " + dev.getHashedPassword());
    System.out.println("Salt: " + dev.getSalt());
    System.out.println("Iterations: " + dev.getPasswordNumIteration());

    String incomingHash = PasswordHasher.hashPassword(
            request.getPlainPassword(),
            dev.getSalt(),
            dev.getPasswordNumIteration()
    );

    System.out.println("Incoming hash: " + incomingHash);
    System.out.println("Password match: " + incomingHash.equals(dev.getHashedPassword()));

    boolean isValid = incomingHash.equals(dev.getHashedPassword());

    if (!isValid) {
        int attempts = dev.getLoginAttempts() + 1;
        dev.setLoginAttempts(attempts);

        if (attempts >= 3) {
            dev.setAccountLocker(true);
            developerRepository.save(dev);
            throw new RuntimeException("Account locked after 3 failed attempts. Please complete CAPTCHA.");
        }

        developerRepository.save(dev);
        throw new RuntimeException("Invalid credentials. Attempts left: " + (3 - attempts));
    }

    dev.setLoginAttempts(0);
    developerRepository.save(dev);

    return dev;
}


    public String unlockDeveloper(CaptchaUnlockRequest request) {
        Developer dev = developerRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Developer not found"));

        if (!dev.isAccountLocker()) {
            return "Account is not locked.";
        }

        if (!"ABC123".equals(request.getCaptchaCode())) {
            throw new RuntimeException("Invalid CAPTCHA code.");
        }

        dev.setAccountLocker(false);
        dev.setLoginAttempts(0);
        developerRepository.save(dev);

        return "Account unlocked successfully.";
    }

    @Autowired
    private CompanyRepository companyRepository;

    public Company registerCompany(CompanyRegisterRequest request) {
        if (companyRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email is already registered.");
        }

        Company company = new Company();
        company.setCompanyName(request.getCompanyName());
        company.setEmail(request.getEmail());

        String salt = PasswordHasher.generateSalt(16);
        int iterations = 65536;
        String hashedPassword = PasswordHasher.hashPassword(request.getPlainPassword(), salt, iterations);

        company.setSalt(salt);
        company.setPasswordNumIteration(iterations);
        company.setHashedPassword(hashedPassword);
        company.setCreatedAt(LocalDate.now());
        company.setUpdatedAt(LocalDate.now());
        company.setLoginAttempts(0);
        company.setAccountLocker(false);

        return companyRepository.save(company);
    }

    public Company loginCompany(CompanyLoginRequest request) {
        Company company = companyRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        if (company.isAccountLocker()) {
            throw new RuntimeException("Account is locked. Please complete CAPTCHA.");
        }

        boolean isValid = PasswordHasher.verifyPassword(
                request.getPlainPassword(),
                company.getSalt(),
                company.getPasswordNumIteration(),
                company.getHashedPassword()
        );

        if (!isValid) {
            int attempts = company.getLoginAttempts() + 1;
            company.setLoginAttempts(attempts);

            if (attempts >= 3) {
                company.setAccountLocker(true);
                companyRepository.save(company);
                throw new RuntimeException("Account locked after 3 failed attempts.");
            }

            companyRepository.save(company);
            throw new RuntimeException("Invalid credentials. Attempts left: " + (3 - attempts));
        }

        company.setLoginAttempts(0);
        companyRepository.save(company);

        return company;
    }

    public String unlockCompany(CaptchaUnlockRequest request) {
        Company company = companyRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        if (!company.isAccountLocker()) {
            return "Account is not locked.";
        }

        if (!"ABC123".equals(request.getCaptchaCode())) {
            throw new RuntimeException("Invalid CAPTCHA code.");
        }

        company.setAccountLocker(false);
        company.setLoginAttempts(0);
        companyRepository.save(company);

        return "Account unlocked successfully.";
    }




}
