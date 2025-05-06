package com.bounteous.bug_bounty_backend.data.entities.bugs;

import com.bounteous.bug_bounty_backend.data.entities.humans.Developer;
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
public class Solution {
    @Id
    @GeneratedValue
    private Long id;
    private String description;
    private String codeLink;
    private SolutionStatus status;
    private LocalDate submittedAt;
    private LocalDate reviewedAt;

    @ManyToOne
    private Developer developer;

    @ManyToOne
    private Bug bug;

    @Builder.Default
    @OneToMany(mappedBy = "solution")
    private List<Feedback> feedbacks = new ArrayList<>();
}
