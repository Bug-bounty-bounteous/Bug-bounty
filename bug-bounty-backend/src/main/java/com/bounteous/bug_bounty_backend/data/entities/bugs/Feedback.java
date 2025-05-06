package com.bounteous.bug_bounty_backend.data.entities.bugs;

import com.bounteous.bug_bounty_backend.data.entities.humans.Company;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@Setter
@Getter
@RequiredArgsConstructor
@Builder
@Entity
public class Feedback {
    @Id
    @GeneratedValue
    private Long id;
    private int rating;
    private String feedbackText;
    private boolean flagged;
    private LocalDate date;

    @ManyToOne
    private Solution solution;

    @ManyToOne
    private Company company;
}
