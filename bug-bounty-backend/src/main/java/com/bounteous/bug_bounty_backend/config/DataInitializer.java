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

            createDevelopers(now);
            createCompanies(now);
            createTechStacks();
            createBugs(now);
            createBugTechStackRelationships();
            createLearningResources(now);

            System.out.println("Data initialization complete.");
        };
    }

    private void createDevelopers(String now) {
        // All developers in compact format
        String[][] developers = {
            // Original developers
            {"1", "Hadiyahia@hadi.com", "hadi.com", "Hadi", "Yahia", "hadiyahia", "0.0", "0"},
            {"3", "sarah@developer.com", "sarah123", "Sarah", "Johnson", "sarahcodes", "0.0", "0"},
            
            // Additional developers
            {"5", "priyanshi@dev.com", "password123", "Priyanshi", "Singh", "priyanshisingh", "0.0", "0"},
            {"6", "eric@dev.com", "password123", "Eric", "Yang", "ericyang", "0.0", "0"},
            {"7", "imad@dev.com", "password123", "Imad", "Issafras", "imadissafras", "0.0", "0"},
            {"8", "lisa@dev.com", "password123", "Lisa", "Anderson", "lisadev", "0.0", "0"},
            {"9", "john@dev.com", "password123", "John", "Davis", "johndavis", "0.0", "0"},
            {"10", "kate@dev.com", "password123", "Kate", "Brown", "katebrown", "0.0", "0"},
            {"11", "david@dev.com", "password123", "David", "Miller", "davidmill", "0.0", "0"},
            {"12", "anna@dev.com", "password123", "Anna", "Garcia", "annagarcia", "0.0", "0"},
            {"13", "tom@dev.com", "password123", "Tom", "Martinez", "tommartinez", "0.0", "0"},
            {"14", "jenny@dev.com", "password123", "Jenny", "Taylor", "jennytaylor", "0.0", "0"}
        };

        for (String[] dev : developers) {
            String hashedPassword = passwordEncoder.encode(dev[2]);
            jdbcTemplate.update(
                "INSERT INTO users (id, email, password, first_name, last_name, role, account_locked, failed_attempts, created_at, updated_at, suspended) "
                + "VALUES (?, ?, ?, ?, ?, 'DEVELOPER', false, 0, ?, ?, false)",
                Long.parseLong(dev[0]), dev[1], hashedPassword, dev[3], dev[4], now, now
            );
            jdbcTemplate.update(
                "INSERT INTO developers (id, username, rating, points) VALUES (?, ?, ?, ?)",
                Long.parseLong(dev[0]), dev[5], Float.parseFloat(dev[6]), Integer.parseInt(dev[7])
            );
        }
    }

    private void createCompanies(String now) {
        // All companies in compact format
        String[][] companies = {
            // Original companies
            {"2", "company@example.com", "password123", "Example", "Company", "Example Tech Corp"},
            {"4", "info@techsolutions.com", "techsolutions123", "Tech", "Solutions", "Tech Solutions Inc"},
            
            // Additional companies
            {"15", "contact@innovate.com", "company123", "Innovate", "Corp", "InnovateCorp"},
            {"16", "hello@startup.com", "company123", "Startup", "Hub", "StartupHub"},
            {"17", "info@cybersec.com", "company123", "Cyber", "Security", "CyberSec Solutions"},
            {"18", "contact@datatech.com", "company123", "Data", "Tech", "DataTech Analytics"}
        };

        for (String[] comp : companies) {
            String hashedPassword = passwordEncoder.encode(comp[2]);
            jdbcTemplate.update(
                "INSERT INTO users (id, email, password, first_name, last_name, role, account_locked, failed_attempts, created_at, updated_at, suspended) "
                + "VALUES (?, ?, ?, ?, ?, 'COMPANY', false, 0, ?, ?, false)",
                Long.parseLong(comp[0]), comp[1], hashedPassword, comp[3], comp[4], now, now
            );
            jdbcTemplate.update(
                "INSERT INTO company (id, company_name) VALUES (?, ?)",
                Long.parseLong(comp[0]), comp[5]
            );
        }
    }

    private void createTechStacks() {
        // Tech stacks in compact format
        String[][] techStacks = {
            {"1", "Java", "Backend"},
            {"2", "Spring Boot", "Backend"},
            {"3", "Angular", "Frontend"},
            {"4", "React", "Frontend"},
            {"5", "Node.js", "Backend"},
            {"6", "Python", "Backend"},
            {"7", "Django", "Backend"},
            {"8", "Vue.js", "Frontend"},
            {"9", "TypeScript", "Language"},
            {"10", "JavaScript", "Language"},
            {"11", "MySQL", "Database"},
            {"12", "MongoDB", "Database"},
            {"13", "PostgreSQL", "Database"},
            {"14", "GraphQL", "API"},
            {"15", "RESTful API", "API"},
            {"16", "Docker", "DevOps"},
            {"17", "Kubernetes", "DevOps"},
            {"18", "AWS", "Cloud"},
            {"19", "Azure", "Cloud"},
            {"20", "Google Cloud", "Cloud"}
        };

        StringBuilder sql = new StringBuilder("INSERT INTO tech_stack (id, name, category) VALUES ");
        for (int i = 0; i < techStacks.length; i++) {
            String[] tech = techStacks[i];
            if (i > 0) sql.append(", ");
            sql.append(String.format("(%s, '%s', '%s')", tech[0], tech[1], tech[2]));
        }
        executeSql(sql.toString());
    }

    private void createBugs(String now) {
        // All bugs in compact format
        String[][] bugs = {
            {"1", "Login Authentication Bypass", "There is a security vulnerability in the login module that allows users to bypass authentication.", "HARD", "500.00", "OPEN", "2", "VERIFIED"},
            {"2", "Form Validation Bug", "The form validation does not correctly validate email addresses, allowing invalid formats.", "EASY", "100.00", "OPEN", "2", "VERIFIED"},
            {"3", "Memory Leak in Backend Service", "The backend service has a memory leak when processing large files, leading to application crashes.", "EXPERT", "1000.00", "OPEN", "4", "VERIFIED"},
            {"4", "UI Rendering Issue in Dashboard", "Charts in the dashboard do not render correctly on Safari browser.", "MEDIUM", "250.00", "OPEN", "15", "VERIFIED"},
            {"5", "API Rate Limiting Bypass", "The API rate limiting mechanism can be bypassed by manipulating request headers.", "HARD", "600.00", "OPEN", "2", "VERIFIED"},
            {"6", "Database Connection Pool Exhaustion", "Under high load, the application exhausts database connections and does not properly release them.", "MEDIUM", "350.00", "OPEN", "16", "VERIFIED"},
            {"7", "XSS Vulnerability in Comments", "User comments are not properly sanitized, allowing cross-site scripting attacks.", "MEDIUM", "400.00", "OPEN", "4", "VERIFIED"},
            {"8", "Responsive Design Breaks on Mobile", "The application UI breaks on mobile devices with screen width less than 320px.", "EASY", "150.00", "OPEN", "2", "VERIFIED"},
            {"9", "JWT Token Not Expiring", "JWT tokens are not expiring as configured, allowing indefinite access.", "HARD", "550.00", "OPEN", "2", "VERIFIED"},
            {"10", "Payment Processing Double Charge", "Under specific conditions, users are charged twice when processing payments.", "EXPERT", "1200.00", "OPEN", "2", "VERIFIED"},
            {"11", "Inefficient Query Performance", "Database queries on the user listing page are inefficient and causing slow performance.", "MEDIUM", "300.00", "OPEN", "17", "VERIFIED"},
            {"12", "File Upload Vulnerability", "The file upload system does not properly validate file types, allowing potential malicious uploads.", "HARD", "700.00", "OPEN", "2", "VERIFIED"}
        };

        StringBuilder sql = new StringBuilder("INSERT INTO bug (id, title, description, difficulty, reward, bug_status, created_at, publisher_id, verification_status) VALUES ");
        for (int i = 0; i < bugs.length; i++) {
            String[] bug = bugs[i];
            if (i > 0) sql.append(", ");
            sql.append(String.format("(%s, '%s', '%s', '%s', %s, '%s', '%s', %s, '%s')",
                bug[0],
                bug[1].replace("'", "''"),
                bug[2].replace("'", "''"),
                bug[3],
                bug[4],
                bug[5],
                now,
                bug[6],
                bug[7]
            ));
        }
        executeSql(sql.toString());
    }

    private void createBugTechStackRelationships() {
        // Bug-TechStack relationships in compact format
        String[][] relationships = {
            {"1", "2"}, {"1", "3"}, {"1", "15"},
            {"2", "3"}, {"2", "10"},
            {"3", "1"}, {"3", "2"},
            {"4", "4"}, {"4", "10"},
            {"5", "2"}, {"5", "15"},
            {"6", "2"}, {"6", "11"},
            {"7", "3"}, {"7", "10"},
            {"8", "4"}, {"8", "10"},
            {"9", "2"}, {"9", "15"},
            {"10", "1"}, {"10", "2"}, {"10", "11"},
            {"11", "2"}, {"11", "11"},
            {"12", "2"}, {"12", "3"}
        };

        StringBuilder sql = new StringBuilder("INSERT INTO bug_tech_stack (bug_id, tech_stack_id) VALUES ");
        for (int i = 0; i < relationships.length; i++) {
            String[] rel = relationships[i];
            if (i > 0) sql.append(", ");
            sql.append(String.format("(%s, %s)", rel[0], rel[1]));
        }
        executeSql(sql.toString());
    }

    private void createLearningResources(String now) {
        // Learning resources in compact format
        String[][] resources = {
            // Tutorial resources
            {"1", "Java Spring Boot Complete Guide", "Comprehensive guide covering Spring Boot fundamentals, REST APIs, security, and database integration", "https://spring.io/guides", "TUTORIAL", "2"},
            {"2", "Angular for Beginners", "Step-by-step tutorial for building modern web applications with Angular", "https://angular.io/tutorial", "TUTORIAL", "2"},
            {"3", "React Hooks Deep Dive", "Advanced tutorial on React Hooks with practical examples", "https://reactjs.org/docs/hooks-intro.html", "TUTORIAL", "4"},
            {"4", "Node.js and Express.js Masterclass", "Complete guide to building RESTful APIs with Node.js and Express", "https://nodejs.org/en/docs/", "TUTORIAL", "15"},
            {"5", "Python Django Web Development", "Full-stack web development with Django framework", "https://docs.djangoproject.com/en/4.2/", "TUTORIAL", "16"},
            
            // Documentation resources
            {"6", "MySQL Official Documentation", "Complete MySQL documentation with best practices", "https://dev.mysql.com/doc/", "DOCUMENTATION", "2"},
            {"7", "MongoDB Manual", "Official MongoDB documentation and guides", "https://docs.mongodb.com/", "DOCUMENTATION", "4"},
            {"8", "PostgreSQL Documentation", "Comprehensive PostgreSQL database documentation", "https://www.postgresql.org/docs/", "DOCUMENTATION", "15"},
            {"9", "Docker Official Docs", "Complete Docker documentation for containerization", "https://docs.docker.com/", "DOCUMENTATION", "16"},
            {"10", "Kubernetes Documentation", "Official Kubernetes documentation and tutorials", "https://kubernetes.io/docs/", "DOCUMENTATION", "17"},
            
            // Video resources
            {"11", "AWS Architecture Patterns", "Video series on AWS cloud architecture best practices", "https://aws.amazon.com/architecture/", "VIDEO", "18"},
            {"12", "TypeScript in 50 Minutes", "Quick introduction to TypeScript for JavaScript developers", "https://www.typescriptlang.org/docs/", "VIDEO", "2"},
            {"13", "GraphQL vs REST APIs", "Comprehensive comparison of GraphQL and REST", "https://graphql.org/learn/", "VIDEO", "4"},
            {"14", "Vue.js 3 Composition API", "Modern Vue.js development with Composition API", "https://vuejs.org/guide/", "VIDEO", "15"},
            {"15", "Docker & Kubernetes DevOps", "Complete DevOps workflow with Docker and Kubernetes", "https://kubernetes.io/training/", "VIDEO", "16"},
            
            // Article resources
            {"16", "Security Best Practices for Web Applications", "Essential security practices every developer should know", "https://owasp.org/www-project-top-ten/", "ARTICLE", "17"},
            {"17", "Database Design Principles", "Fundamental principles of database design and normalization", "https://www.databasestar.com/database-design/", "ARTICLE", "18"},
            {"18", "Microservices Architecture Patterns", "Key patterns and practices for microservices architecture", "https://microservices.io/patterns/", "ARTICLE", "2"},
            {"19", "API Design Best Practices", "Guidelines for designing robust and maintainable APIs", "https://swagger.io/resources/articles/best-practices/", "ARTICLE", "4"},
            {"20", "Performance Optimization Techniques", "Strategies for optimizing application performance", "https://web.dev/fast/", "ARTICLE", "15"},
            
            // Code example resources
            {"21", "Spring Security Implementation Examples", "Real-world examples of Spring Security configurations", "https://spring.io/projects/spring-security", "CODE_EXAMPLE", "16"},
            {"22", "React Testing Library Examples", "Comprehensive testing examples for React applications", "https://testing-library.com/docs/react-testing-library/example-intro", "CODE_EXAMPLE", "17"},
            {"23", "Python Data Science Snippets", "Common data science operations in Python", "https://pandas.pydata.org/docs/", "CODE_EXAMPLE", "18"},
            {"24", "JavaScript ES6+ Code Samples", "Modern JavaScript examples and best practices", "https://developer.mozilla.org/en-US/docs/Web/JavaScript", "CODE_EXAMPLE", "2"},
            {"25", "SQL Query Optimization Examples", "Advanced SQL queries and optimization techniques", "https://use-the-index-luke.com/", "CODE_EXAMPLE", "4"},
            
            // Additional diverse resources
            {"26", "Azure Cloud Computing Fundamentals", "Introduction to Microsoft Azure cloud services", "https://docs.microsoft.com/en-us/azure/", "TUTORIAL", "15"},
            {"27", "Google Cloud Platform Guide", "Complete guide to Google Cloud Platform services", "https://cloud.google.com/docs", "DOCUMENTATION", "16"},
            {"28", "Machine Learning with Python", "Practical machine learning implementation guide", "https://scikit-learn.org/stable/", "TUTORIAL", "17"},
            {"29", "Cybersecurity Fundamentals", "Essential cybersecurity concepts for developers", "https://www.sans.org/reading-room/", "ARTICLE", "18"},
            {"30", "Agile Development Practices", "Agile methodologies and implementation strategies", "https://agilemanifesto.org/", "ARTICLE", "2"}
        };

        StringBuilder sql = new StringBuilder("INSERT INTO learning_resources (id, title, description, url, resource_type, created_at, reported, publisher_id) VALUES ");
        
        for (int i = 0; i < resources.length; i++) {
            String[] resource = resources[i];
            if (i > 0) sql.append(", ");
            sql.append(String.format("(%s, '%s', '%s', '%s', '%s', '%s', false, %s)",
                resource[0],
                resource[1].replace("'", "''"),
                resource[2].replace("'", "''"),
                resource[3],
                resource[4],
                now,
                resource[5]
            ));
        }
        
        executeSql(sql.toString());
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