package com.bounteous.bug_bounty_backend.entities.humans;

import com.bounteous.bug_bounty_backend.entities.bugs.Bug;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class Company extends User {
    private String companyName;

    @OneToMany(mappedBy = "publisher")
    private List<Bug> bugs;
}
