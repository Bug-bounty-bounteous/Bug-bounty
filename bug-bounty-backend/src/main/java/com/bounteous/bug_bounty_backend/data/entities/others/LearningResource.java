package com.bounteous.bug_bounty_backend.data.entities.others;

import com.bounteous.bug_bounty_backend.data.entities.humans.Company;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "learning_resources")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearningResource {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(nullable = false, length = 1000)
    private String description;
    
    @Column(length = 500)
    private String url;
    
    @Column(name = "file_name", length = 255)
    private String fileName;
    
    @Column(name = "file_path", length = 500)
    private String filePath;
    
    @Column(name = "file_size")
    private Long fileSize;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResourceType resourceType;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime date = LocalDateTime.now();
    
    @Builder.Default
    private boolean reported = false;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "publisher_id")
    @JsonIgnoreProperties({"resources", "bugs", "feedbacks"})
    private Company publisher;
    
    /**
     * Check if this resource is a file or external link
     */
    @Transient
    public boolean isFileResource() {
        return fileName != null && filePath != null;
    }
    
    /**
     * Get the resource URL (either external URL or file download URL)
     */
    @Transient
    public String getResourceUrl() {
        if (isFileResource()) {
            return "/api/resources/" + id + "/download";
        }
        return url;
    }
}