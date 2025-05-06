package com.bounteous.bug_bounty_backend.entities.humans;

import com.bounteous.bug_bounty_backend.entities.bugs.BugClaim;
import com.bounteous.bug_bounty_backend.entities.bugs.Solution;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@Entity
public class Developer extends User {
    private String Username;
    private Float rating;
    @OneToMany(mappedBy = "developer")
    private List<BugClaim> bugClaims = new ArrayList<>();
    @OneToMany(mappedBy = "developer")
    private List<Solution> solutions = new ArrayList<>();
}
