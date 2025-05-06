package com.bounteous.bug_bounty_backend.entities.bugs;

import com.bounteous.bug_bounty_backend.entities.humans.Developer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@IdClass(BugClaimId.class)
public class BugClaim {
    @Id
    @ManyToOne
    private Developer developer;

    @Id
    @ManyToOne
    private Bug bug;

    private LocalDate date;
    private ClaimStatus claimStatus;
}
