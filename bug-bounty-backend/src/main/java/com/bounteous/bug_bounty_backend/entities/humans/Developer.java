package com.bounteous.bug_bounty_backend.entities.humans;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
public class Developer extends User {
    private String Username;
    private Float rating;
}
