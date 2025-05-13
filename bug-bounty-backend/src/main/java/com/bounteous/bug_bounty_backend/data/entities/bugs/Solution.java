package com.bounteous.bug_bounty_backend.data.entities.bugs;

import com.bounteous.bug_bounty_backend.data.entities.humans.Developer;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Blob;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Solution {
    @Id
    @GeneratedValue
    private Long id;

    private String description;
    private String codeLink;
    private Blob file;

    @Enumerated(EnumType.STRING)
    private SolutionStatus status;

    private LocalDateTime submittedAt;
    private LocalDateTime reviewedAt;

    @ManyToOne
    @ToString.Exclude
    private Developer developer;

    @ManyToOne
    @ToString.Exclude
    private Bug bug;

    @OneToMany(mappedBy = "solution")
    @Builder.Default
    @ToString.Exclude
    private List<Feedback> feedbacks = new ArrayList<>();
}