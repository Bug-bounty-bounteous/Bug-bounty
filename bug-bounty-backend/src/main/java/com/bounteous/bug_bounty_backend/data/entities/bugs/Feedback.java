package com.bounteous.bug_bounty_backend.data.entities.bugs;

import com.bounteous.bug_bounty_backend.data.entities.humans.Company;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

// Feedback entity for solutions
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feedback {
    @Id
    @GeneratedValue
    private Long id;
    
    private int rating;

    private boolean flagged;
    private LocalDateTime submittedAt;

    @ManyToOne
    private Solution solution;

    @ManyToOne
    private Company company;

    @Column(length = 2000)
    private String feedbackMessage;
}
