package com.bounteous.bug_bounty_backend.data.entities.bugs;

import com.bounteous.bug_bounty_backend.data.entities.humans.Developer;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

// Links bugs to developers who claimed them
@Entity
@IdClass(BugClaimId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BugClaim {
    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    private Developer developer;

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    private Bug bug;

    private LocalDateTime date;
    
    @Enumerated(EnumType.STRING)
    private ClaimStatus claimStatus;

    private String claimNote;
}
