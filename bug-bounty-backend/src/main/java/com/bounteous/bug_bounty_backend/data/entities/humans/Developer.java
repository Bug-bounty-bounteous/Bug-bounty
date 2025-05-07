package com.bounteous.bug_bounty_backend.data.entities.humans;

import com.bounteous.bug_bounty_backend.data.entities.bugs.BugClaim;
import com.bounteous.bug_bounty_backend.data.entities.bugs.Solution;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Developer extends User {
    private String username;
    private Float rating;
    private Integer points;

    @OneToMany(mappedBy = "developer")
    @Builder.Default 
    private List<BugClaim> bugClaims = new ArrayList<>();

    @OneToMany(mappedBy = "developer")
    @Builder.Default 
    private List<Solution> solutions = new ArrayList<>();
}
