package com.bounteous.bug_bounty_backend.data.dto.responses.leaderboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

import com.bounteous.bug_bounty_backend.data.dto.responses.bug.BugResponse;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardResponse {

    private List<DeveloperInfo> developers;

    private int myRank;
    private float myRating;
    private int myPoints;
    private int length;
    private String firstName;
    private String lastName;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeveloperInfo {

        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private int points;
        private float rating;
        private List<BugResponse> claimedBugs;
    }
}
