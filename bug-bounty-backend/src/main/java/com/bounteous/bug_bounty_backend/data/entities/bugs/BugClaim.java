package com.bounteous.bug_bounty_backend.data.entities.bugs;

import com.bounteous.bug_bounty_backend.data.entities.humans.Developer;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@Setter
@Getter
@RequiredArgsConstructor
@Builder
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
