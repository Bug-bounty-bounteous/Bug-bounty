package com.bounteous.bug_bounty_backend.data.entities.humans;

import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@Setter
@Getter
@RequiredArgsConstructor
//@ToString(callSuper = true)
@SuperBuilder
@Entity
public class Admin extends User {
    private String something;
}
