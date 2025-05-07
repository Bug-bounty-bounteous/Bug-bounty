package com.bounteous.bug_bounty_backend.data.entities.bugs;

import com.bounteous.bug_bounty_backend.data.entities.humans.Company;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bug {
    @Id
    @GeneratedValue
    private Long id;
    private String title;
    private String description;
    
    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;
    
    private double reward;
    
    @Enumerated(EnumType.STRING)
    private BugStatus bugStatus;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @ManyToOne
    private Company publisher;
    
    @Enumerated(EnumType.STRING)
    private VerificationStatus verificationStatus;
    
    private String verificationReport;

    @ManyToMany(mappedBy = "bugs")
    @Builder.Default
    private List<TechStack> stack = new ArrayList<>();

    @OneToMany(mappedBy = "bug")
    @Builder.Default
    private List<BugClaim> bugClaims = new ArrayList<>();

    @OneToMany(mappedBy = "bug")
    @Builder.Default
    private List<Solution> solutions = new ArrayList<>();
}
