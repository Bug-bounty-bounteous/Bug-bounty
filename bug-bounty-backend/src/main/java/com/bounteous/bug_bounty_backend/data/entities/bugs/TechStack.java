package com.bounteous.bug_bounty_backend.data.entities.bugs;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TechStack {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String category;

    @ManyToMany
    @JoinTable(
        name = "bug_tech_stack",
        joinColumns = @JoinColumn(name = "tech_stack_id"),
        inverseJoinColumns = @JoinColumn(name = "bug_id")
    )
    @Builder.Default
    private List<Bug> bugs = new ArrayList<>();
}
