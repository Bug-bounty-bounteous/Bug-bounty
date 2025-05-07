package com.bounteous.bug_bounty_backend.data.entities.bugs;

import lombok.*;

import java.io.Serializable;

// Composite key for BugClaim entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class BugClaimId implements Serializable {
    private Long developer;
    private Long bug;
}
