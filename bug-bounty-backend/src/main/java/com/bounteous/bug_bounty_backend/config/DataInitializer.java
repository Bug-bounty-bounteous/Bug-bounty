package com.bounteous.bug_bounty_backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    @Bean
    @Transactional
    public CommandLineRunner initData(DataSource dataSource) {
        return args -> {
            // Check if data already exists
            if (countRows("users") > 0 || countRows("tech_stack") > 0) {
                System.out.println("Data already exists. Skipping initialization.");
                return; // Skip initialization if data exists
            }

            System.out.println("Initializing sample data...");

            // Format current datetime for SQL
            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            // Create developers
            String developerPassword = passwordEncoder.encode("hadi.com");
            jdbcTemplate.update(
                    "INSERT INTO users (id, email, password, first_name, last_name, role, account_locked, failed_attempts, created_at, updated_at, suspended) "
                    + "VALUES (1, 'Hadiyahia@hadi.com', ?, 'Hadi', 'Yahia', 'DEVELOPER', false, 0, ?, ?, false)",
                    developerPassword, now, now
            );

            jdbcTemplate.update(
                    "INSERT INTO developers (id, username, rating, points) VALUES (1, 'hahaha', 0.0, 0)"
            );

            String developer2Password = passwordEncoder.encode("sarah123");
            jdbcTemplate.update(
                    "INSERT INTO users (id, email, password, first_name, last_name, role, account_locked, failed_attempts, created_at, updated_at, suspended) "
                    + "VALUES (3, 'sarah@developer.com', ?, 'Sarah', 'Johnson', 'DEVELOPER', false, 0, ?, ?, false)",
                    developer2Password, now, now
            );

            jdbcTemplate.update(
                    "INSERT INTO developers (id, username, rating, points) VALUES (3, 'sarahcodes', 0.0, 0)"
            );

            // Create company user
            String companyPassword = passwordEncoder.encode("password123");
            jdbcTemplate.update(
                    "INSERT INTO users (id, email, password, first_name, last_name, role, account_locked, failed_attempts, created_at, updated_at, suspended) "
                    + "VALUES (2, 'company@example.com', ?, 'Example', 'Company', 'COMPANY', false, 0, ?, ?, false)",
                    companyPassword, now, now
            );

//            jdbcTemplate.update(
//                "INSERT INTO company (id, company_name) VALUES (2, 'Example Tech Corp')"
//            );
            String company2Password = passwordEncoder.encode("techsolutions123");
            jdbcTemplate.update(
                    "INSERT INTO users (id, email, password, first_name, last_name, role, account_locked, failed_attempts, created_at, updated_at, suspended) "
                    + "VALUES (4, 'info@techsolutions.com', ?, 'Tech', 'Solutions', 'COMPANY', false, 0, ?, ?, false)",
                    company2Password, now, now
            );

//            jdbcTemplate.update(
//                "INSERT INTO company (id, company_name) VALUES (4, 'Tech Solutions Inc')"
//            );
//
            // Create tech stacks using SQL
            executeSql(
                    "INSERT INTO tech_stack (id, name, category) VALUES "
                    + "(1, 'Java', 'Backend'), "
                    + "(2, 'Spring Boot', 'Backend'), "
                    + "(3, 'Angular', 'Frontend'), "
                    + "(4, 'React', 'Frontend'), "
                    + "(5, 'Node.js', 'Backend'), "
                    + "(6, 'Python', 'Backend'), "
                    + "(7, 'Django', 'Backend'), "
                    + "(8, 'Vue.js', 'Frontend'), "
                    + "(9, 'TypeScript', 'Language'), "
                    + "(10, 'JavaScript', 'Language'), "
                    + "(11, 'MySQL', 'Database'), "
                    + "(12, 'MongoDB', 'Database'), "
                    + "(13, 'PostgreSQL', 'Database'), "
                    + "(14, 'GraphQL', 'API'), "
                    + "(15, 'RESTful API', 'API'), "
                    + "(16, 'Docker', 'DevOps'), "
                    + "(17, 'Kubernetes', 'DevOps'), "
                    + "(18, 'AWS', 'Cloud'), "
                    + "(19, 'Azure', 'Cloud'), "
                    + "(20, 'Google Cloud', 'Cloud')"
            );

            // Create bugs using SQL
            executeSql(
                    "INSERT INTO bug (id, title, description, difficulty, reward, bug_status, created_at, publisher_id, verification_status) VALUES "
                    + "(1, 'Login Authentication Bypass', 'There is a security vulnerability in the login module that allows users to bypass authentication.', 'HARD', 500.00, 'OPEN', '" + now + "', 2, 'VERIFIED'), "
                    + "(2, 'Form Validation Bug', 'The form validation does not correctly validate email addresses, allowing invalid formats.', 'EASY', 100.00, 'OPEN', '" + now + "', 2, 'VERIFIED'), "
                    + "(3, 'Memory Leak in Backend Service', 'The backend service has a memory leak when processing large files, leading to application crashes.', 'EXPERT', 1000.00, 'OPEN', '" + now + "', 2, 'VERIFIED'), "
                    + "(4, 'UI Rendering Issue in Dashboard', 'Charts in the dashboard do not render correctly on Safari browser.', 'MEDIUM', 250.00, 'OPEN', '" + now + "', 2, 'VERIFIED'), "
                    + "(5, 'API Rate Limiting Bypass', 'The API rate limiting mechanism can be bypassed by manipulating request headers.', 'HARD', 600.00, 'OPEN', '" + now + "', 2, 'VERIFIED'), "
                    + "(6, 'Database Connection Pool Exhaustion', 'Under high load, the application exhausts database connections and does not properly release them.', 'MEDIUM', 350.00, 'OPEN', '" + now + "', 2, 'VERIFIED'), "
                    + "(7, 'XSS Vulnerability in Comments', 'User comments are not properly sanitized, allowing cross-site scripting attacks.', 'MEDIUM', 400.00, 'OPEN', '" + now + "', 2, 'VERIFIED'), "
                    + "(8, 'Responsive Design Breaks on Mobile', 'The application UI breaks on mobile devices with screen width less than 320px.', 'EASY', 150.00, 'OPEN', '" + now + "', 2, 'VERIFIED'), "
                    + "(9, 'JWT Token Not Expiring', 'JWT tokens are not expiring as configured, allowing indefinite access.', 'HARD', 550.00, 'OPEN', '" + now + "', 2, 'VERIFIED'), "
                    + "(10, 'Payment Processing Double Charge', 'Under specific conditions, users are charged twice when processing payments.', 'EXPERT', 1200.00, 'OPEN', '" + now + "', 2, 'VERIFIED'), "
                    + "(11, 'Inefficient Query Performance', 'Database queries on the user listing page are inefficient and causing slow performance.', 'MEDIUM', 300.00, 'OPEN', '" + now + "', 2, 'VERIFIED'), "
                    + "(12, 'File Upload Vulnerability', 'The file upload system does not properly validate file types, allowing potential malicious uploads.', 'HARD', 700.00, 'OPEN', '" + now + "', 2, 'VERIFIED')"
            );

            // Create bug-techstack relationships using SQL (with DISTINCT to avoid duplicates)
            executeSql(
                    "INSERT INTO bug_tech_stack (bug_id, tech_stack_id) VALUES "
                    + "(1, 2), (1, 3), (1, 15), "
                    + // Bug 1: Spring Boot, Angular, RESTful API
                    "(2, 3), (2, 10), "
                    + // Bug 2: Angular, JavaScript
                    "(3, 1), (3, 2), "
                    + // Bug 3: Java, Spring Boot
                    "(4, 3), (4, 10), "
                    + // Bug 4: Angular, JavaScript
                    "(5, 2), (5, 15), "
                    + // Bug 5: Spring Boot, RESTful API
                    "(6, 2), (6, 11), "
                    + // Bug 6: Spring Boot, MySQL
                    "(7, 3), (7, 10), "
                    + // Bug 7: Angular, JavaScript
                    "(8, 3), (8, 10), "
                    + // Bug 8: Angular, JavaScript
                    "(9, 2), (9, 15), "
                    + // Bug 9: Spring Boot, RESTful API
                    "(10, 1), (10, 2), (10, 11), "
                    + // Bug 10: Java, Spring Boot, MySQL
                    "(11, 2), (11, 11), "
                    + // Bug 11: Spring Boot, MySQL
                    "(12, 2), (12, 3)" // Bug 12: Spring Boot, Angular
            );

            System.out.println("Data initialization complete.");
        };
    }

    private int countRows(String tableName) {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + tableName, Integer.class);
    }

    private void executeSql(String sql) {
        try {
            jdbcTemplate.execute(sql);
        } catch (Exception e) {
            System.err.println("Error executing SQL: " + e.getMessage());
            throw e;
        }
    }
}
