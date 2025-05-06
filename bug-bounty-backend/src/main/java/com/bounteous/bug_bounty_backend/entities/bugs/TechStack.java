package com.bounteous.bug_bounty_backend.entities.bugs;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

import java.util.ArrayList;
import java.util.List;

@Entity
public class TechStack {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String category;
    @ManyToMany
    private List<Bug> bugs = new ArrayList<>();
}
