package com.bounteous.bug_bounty_backend.data;

import com.bounteous.bug_bounty_backend.data.entities.bugs.BugClaim;
import com.bounteous.bug_bounty_backend.data.entities.humans.Developer;
import com.bounteous.bug_bounty_backend.data.repositories.bugs.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

@DataJpaTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
public class EntityTests {
    @Autowired
    private BugClaimRepository bugClaimRepository;
    @Autowired
    private BugRepository bugRepository;
    @Autowired
    private FeedbackRepository feedbackRepository;
    @Autowired
    private SolutionRepository solutionRepository;
    @Autowired
    private TechStackRepository techStackRepository;

    @BeforeEach
    public void setup() {
        // entities

        // relationships
        
        // save
    }

    @Test
    public void doNothing() {}


}
