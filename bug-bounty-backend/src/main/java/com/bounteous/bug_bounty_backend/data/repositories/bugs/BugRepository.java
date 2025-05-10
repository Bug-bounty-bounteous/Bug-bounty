package com.bounteous.bug_bounty_backend.data.repositories.bugs;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bounteous.bug_bounty_backend.data.entities.bugs.Bug;
import com.bounteous.bug_bounty_backend.data.entities.humans.Company;

import jakarta.persistence.LockModeType;

@Repository
public interface BugRepository extends JpaRepository<Bug, Long>, JpaSpecificationExecutor<Bug> {

    Optional<Bug> findByTitle(String title);

    Optional<Bug> findByDescription(String description);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Bug b WHERE b.id = :id AND b.bugStatus = 'OPEN'")
    Optional<Bug> findOpenBugByIdWithLock(@Param("id") Long id);

    List<Bug> findByPublisher(Company company);
}
