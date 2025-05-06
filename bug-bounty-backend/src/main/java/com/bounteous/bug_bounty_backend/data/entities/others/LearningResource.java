package com.bounteous.bug_bounty_backend.data.entities.others;

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
public class LearningResource {
    @Id
    @GeneratedValue
    private Long id;
    private String title;
    private String description;
    private String url;
    private RessourceType ressourceType;
    private LocalDate date;
    private boolean reported;
    @ManyToOne
    private Company publisher;
}
