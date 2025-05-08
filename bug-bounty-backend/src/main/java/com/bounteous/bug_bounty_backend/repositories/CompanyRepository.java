package com.bounteous.bug_bounty_backend.repositories;

import com.bounteous.bug_bounty_backend.entities.humans.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByEmail(String email);
}
