package com.bounteous.bug_bounty_backend.data.entities.humans;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@AllArgsConstructor
@RequiredArgsConstructor
@SuperBuilder
@ToString
@MappedSuperclass
public class User {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String email;
    // Refer to v for why the following are a needed
    // https://howtodoinjava.com/java/java-security/how-to-generate-secure-password-hash-md5-sha-pbkdf2-bcrypt-examples/#4-1-java-pbkdf2withhmacsha1-hash-example
    // Look into Spring Security instead tho
    private String hashedPassword;
    private String salt;
    private int passwordNumIteration;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private int loginAttempts;
    private boolean accountLocker;
}
