package com.bounteous.bug_bounty_backend.data.entities.humans;

import com.bounteous.bug_bounty_backend.data.entities.bugs.BugClaim;
import com.bounteous.bug_bounty_backend.data.entities.bugs.Solution;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Setter
@Getter
@RequiredArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@Entity
public class Developer extends User {
    private String username;
    private Float rating;
    @Builder.Default
    @ToString.Exclude
	@OneToMany(mappedBy = "developer")
    private List<BugClaim> bugClaims = new ArrayList<>();
    @Builder.Default
    @ToString.Exclude
	@OneToMany(mappedBy = "developer")
    private List<Solution> solutions = new ArrayList<>();
}
