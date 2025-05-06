package com.bounteous.bug_bounty_backend.data.entities.bugs;

import com.bounteous.bug_bounty_backend.data.entities.humans.Company;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Setter
@Getter
@RequiredArgsConstructor
@Builder
@Entity
public class Bug {
    @Id
    @GeneratedValue
    private Long id;
    private String title;
    private String description;
    private Difficulty difficulty;
    private double reward;
    private BugStatus bugStatus;
    private LocalDate creationAt;
    private LocalDate updatedAt;
    @ManyToOne
    private Company publisher;
    @Builder.Default
    @ManyToMany(mappedBy = "bugs")
    private List<TechStack> stack = new ArrayList<>();
    @Builder.Default
    @OneToMany(mappedBy = "bug")
    private List<BugClaim> bugClaims = new ArrayList<>();
    @Builder.Default
    @OneToMany(mappedBy = "bug")
    private List<Solution> solutions = new ArrayList<>();
}
