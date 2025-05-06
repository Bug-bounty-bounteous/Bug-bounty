package com.bounteous.bug_bounty_backend.entities.bugs;

import com.bounteous.bug_bounty_backend.entities.humans.Company;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
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
    @ManyToMany(mappedBy = "bugs")
    private List<TechStack> stack = new ArrayList<>();
    @OneToMany(mappedBy = "bug")
    private List<BugClaim> bugClaims = new ArrayList<>();
    @OneToMany(mappedBy = "bug")
    private List<Solution> solutions = new ArrayList<>();
}
