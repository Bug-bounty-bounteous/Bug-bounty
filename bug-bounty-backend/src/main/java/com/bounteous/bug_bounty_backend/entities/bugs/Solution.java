package com.bounteous.bug_bounty_backend.entities.bugs;

import com.bounteous.bug_bounty_backend.entities.humans.Developer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
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

    @OneToMany(mappedBy = "solution")
    private List<Feedback> feedbacks = new ArrayList<>();
}
