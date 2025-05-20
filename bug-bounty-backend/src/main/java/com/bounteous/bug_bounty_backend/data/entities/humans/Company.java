package com.bounteous.bug_bounty_backend.data.entities.humans;

import com.bounteous.bug_bounty_backend.data.entities.bugs.Bug;
import com.bounteous.bug_bounty_backend.data.entities.bugs.Feedback;
import com.bounteous.bug_bounty_backend.data.entities.others.LearningResource;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Company extends User {
    private String companyName;

    @OneToMany(mappedBy = "publisher", fetch = FetchType.EAGER)
    @Builder.Default 
    private List<Bug> bugs = new ArrayList<>();

    @OneToMany(mappedBy = "publisher")
    @Builder.Default 
    private List<LearningResource> resources = new ArrayList<>();

    @OneToMany(mappedBy = "company")
    @Builder.Default 
    private List<Feedback> feedbacks = new ArrayList<>();

    public boolean publishedBug(Bug bug) {
        return getBugs().contains(bug);
    }
}
