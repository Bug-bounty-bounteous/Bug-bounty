package com.bounteous.bug_bounty_backend.data.entities.bugs;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@Setter
@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class BugClaimId implements Serializable {
    private Long developer;
    private Long bug;
}
