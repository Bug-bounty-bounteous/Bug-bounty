package com.bounteous.bug_bounty_backend.entities.others;

import com.bounteous.bug_bounty_backend.entities.humans.Company;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
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
