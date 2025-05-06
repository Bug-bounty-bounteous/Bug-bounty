package com.bounteous.bug_bounty_backend.entities.humans;

import com.bounteous.bug_bounty_backend.entities.bugs.Bug;
import com.bounteous.bug_bounty_backend.entities.bugs.Feedback;
import com.bounteous.bug_bounty_backend.entities.others.LearningResource;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Company extends User {
    private String companyName;

    @OneToMany(mappedBy = "publisher")
    private List<Bug> bugs = new ArrayList<>();
    @OneToMany(mappedBy = "publisher")
    private List<LearningResource> resources = new ArrayList<>();
    @OneToMany(mappedBy = "company")
    private List<Feedback> feedbacks = new ArrayList<>();
}
