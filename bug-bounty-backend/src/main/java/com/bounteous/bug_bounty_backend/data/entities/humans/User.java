package com.bounteous.bug_bounty_backend.data.entities.humans;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

// Base user entity with common properties
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@MappedSuperclass
public abstract class User {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String email;
    private String hashedPassword;
    private String salt;
    private int passwordNumIteration;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int loginAttempts;
    private boolean accountLocked;
}
