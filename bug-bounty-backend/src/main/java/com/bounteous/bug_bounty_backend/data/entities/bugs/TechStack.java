package com.bounteous.bug_bounty_backend.data.entities.bugs;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Setter
@Getter
@RequiredArgsConstructor
@Builder
@Entity
public class TechStack {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String category;
    @Builder.Default
    @ManyToMany
    private List<Bug> bugs = new ArrayList<>();
}
