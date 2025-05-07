package com.bounteous.bug_bounty_backend.data.entities.others;

import com.bounteous.bug_bounty_backend.data.entities.humans.Company;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

// Learning resource entity
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearningResource {
    @Id
    @GeneratedValue
    private Long id;
    
    private String title;
    private String description;
    private String url;
    
    @Enumerated(EnumType.STRING)
    private ResourceType resourceType;
    
    private LocalDateTime date;
    private boolean reported;
    
    @ManyToOne
    private Company publisher;
}
