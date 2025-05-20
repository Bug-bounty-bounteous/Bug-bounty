package com.bounteous.bug_bounty_backend.data.entities.bugs;

import com.bounteous.bug_bounty_backend.data.entities.humans.Company;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bug {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    
    @ManyToOne(fetch = FetchType.EAGER)
    private Company publisher;
    
    @Enumerated(EnumType.STRING)
    private VerificationStatus verificationStatus;
    
    private String verificationReport;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "bug_tech_stack",
        joinColumns = @JoinColumn(name = "bug_id"),
        inverseJoinColumns = @JoinColumn(name = "tech_stack_id")
    )
    @Builder.Default
    private List<TechStack> stack = new ArrayList<>();

    @OneToMany(mappedBy = "bug", fetch = FetchType.EAGER)
    @Builder.Default
    private List<BugClaim> bugClaims = new ArrayList<>();

    @OneToMany(mappedBy = "bug", fetch = FetchType.EAGER)
    @Builder.Default
    private List<Solution> solutions = new ArrayList<>();

    @Override
    public boolean equals(Object other) {
        if (! (other instanceof Bug)) return false;
        if (this == other) return true;
        return Objects.equals(((Bug) other).getId(), this.getId());
    }
}