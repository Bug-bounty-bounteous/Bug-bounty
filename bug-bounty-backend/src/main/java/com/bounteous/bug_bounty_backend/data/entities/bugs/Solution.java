package com.bounteous.bug_bounty_backend.data.entities.bugs;

import com.bounteous.bug_bounty_backend.data.entities.humans.Developer;
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
public class Solution {
    @Id
    @GeneratedValue
    private Long id;
    
    private String description;
    private String codeLink;
    
    @Enumerated(EnumType.STRING)
    private SolutionStatus status;
    
    private LocalDateTime submittedAt;
    private LocalDateTime reviewedAt;

    @ManyToOne
    private Developer developer;

    @ManyToOne
    private Bug bug;
    
    @OneToMany(mappedBy = "solution")
    @Builder.Default
    private List<Feedback> feedbacks = new ArrayList<>();
}
