package com.bounteous.bug_bounty_backend.data.entities.humans;

import com.bounteous.bug_bounty_backend.data.entities.bugs.Bug;
import com.bounteous.bug_bounty_backend.data.entities.bugs.BugClaim;
import com.bounteous.bug_bounty_backend.data.entities.bugs.Solution;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "developers")
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

    public boolean isClaiming(Bug bug) {
        return ! (getBugClaims().stream().filter(
                bugClaim -> bugClaim.getBug().equals(bug)
        ).toList().isEmpty());
    }
}
