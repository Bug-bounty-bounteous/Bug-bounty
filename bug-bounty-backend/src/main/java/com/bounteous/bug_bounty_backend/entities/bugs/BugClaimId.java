package com.bounteous.bug_bounty_backend.entities.bugs;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
public class BugClaimId implements Serializable {
    private Long developer;
    private Long bug;
}
