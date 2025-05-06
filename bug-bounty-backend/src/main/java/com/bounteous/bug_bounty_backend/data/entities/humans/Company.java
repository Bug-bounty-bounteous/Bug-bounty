package com.bounteous.bug_bounty_backend.data.entities.humans;

import com.bounteous.bug_bounty_backend.data.entities.bugs.Bug;
import com.bounteous.bug_bounty_backend.data.entities.bugs.Feedback;
import com.bounteous.bug_bounty_backend.data.entities.others.LearningResource;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Setter
@Getter
@RequiredArgsConstructor
//@ToString(callSuper = true)
@SuperBuilder
@Entity
public class Company extends User {
    private String companyName;

    @Builder.Default
    @OneToMany(mappedBy = "publisher")
    private List<Bug> bugs = new ArrayList<>();
    @Builder.Default
    @OneToMany(mappedBy = "publisher")
    private List<LearningResource> resources = new ArrayList<>();
    @Builder.Default
    @OneToMany(mappedBy = "company")
    private List<Feedback> feedbacks = new ArrayList<>();
}
