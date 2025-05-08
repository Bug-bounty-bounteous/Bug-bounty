package com.bounteous.bug_bounty_backend.data.repositories.humans;

import com.bounteous.bug_bounty_backend.data.entities.humans.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByEmail(String email);
    Optional<Company> findByCompanyName(String companyName);
}
