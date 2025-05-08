// package com.bounteous.bug_bounty_backend.data;

// import com.bounteous.bug_bounty_backend.data.entities.bugs.*;
// import com.bounteous.bug_bounty_backend.data.entities.humans.Company;
// import com.bounteous.bug_bounty_backend.data.entities.humans.Developer;
// import com.bounteous.bug_bounty_backend.data.entities.others.LearningResource;
// import com.bounteous.bug_bounty_backend.data.repositories.bugs.*;
// import com.bounteous.bug_bounty_backend.data.repositories.humans.AdminRepository;
// import com.bounteous.bug_bounty_backend.data.repositories.humans.CompanyRepository;
// import com.bounteous.bug_bounty_backend.data.repositories.humans.DeveloperRepository;
// import com.bounteous.bug_bounty_backend.data.repositories.others.LearningResourceRepository;
// import org.junit.jupiter.api.Assertions;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

// import java.util.List;

// /**
//  * Testing a scenario, see setup comment
//  */
// @DataJpaTest(properties = {
//         "spring.datasource.url=jdbc:h2:mem:testdb",
//         "spring.jpa.hibernate.ddl-auto=create-drop"
// }, showSql = false)
// public class EntityTests {
//     @Autowired
//     private BugClaimRepository bugClaimRepository;
//     @Autowired
//     private BugRepository bugRepository;
//     @Autowired
//     private FeedbackRepository feedbackRepository;
//     @Autowired
//     private SolutionRepository solutionRepository;
//     @Autowired
//     private TechStackRepository techStackRepository;
//     @Autowired
//     private AdminRepository adminRepository;
//     @Autowired
//     private CompanyRepository companyRepository;
//     @Autowired
//     private DeveloperRepository developerRepository;
//     @Autowired
//     private LearningResourceRepository learningResourceRepository;


//     @BeforeEach
//     public void setup() {
//         // Story line:
//         //      Users:
//         //          Devs:
//         //              Imad, Eric, Hadi, and Priyanshi
//         //          Companies:
//         //              Bounteous and Morgan Stanley
//         //      Learning Ressources:
//         //          Bounteous has published 2 learning ressources:
//         //              Spring Boot debuging
//         //              Hibernate
//         //          Morgan Stanley hasn't published any
//         //      Bugs:
//         //          Bounteous has 1 bug:
//         //              Spring Boot vulnerability
//         //                  1 TechStacks:
//         //                      Spring Boot
//         //          Morgan Stanley has 2 bugs:
//         //              Free Money Machine:
//         //                  2 TechStacks:
//         //                      Fortran
//         //                      Python
//         //              Strategy Leak:
//         //                  No Tech Stack
//         //      Claims:
//         //          Imad claimed all bugs (He fails to submit a solution for all of them)
//         //          Eric claims Spring Boot Vulnerability
//         //              Submits a solution:
//         //                  "Stop using Spring Boot!"
//         //                  Bounteous gives feedbacks:
//         //                      "No :("
//         //                      "Ok maybe we will"
//         //          Hadi claims strategy Leak and Spring Boot vulnerability
//         //              Submits a solution for strategy leak:
//         //                  "Just don't"
//         //                  Morgan Stanley doesn't give feedback
//         //          Priyanshi claims Free Money Machine:
//         //              Submits a solution:
//         //                  "Turn off the machine"
//         //                  Morgan Stanley gives feedback:
//         //                      "Oh"

//         // entities
//         Developer imad = Developer.builder()
//                 .name("Imad").build();
//         Developer eric = Developer.builder()
//                 .name("Eric").build();
//         Developer hadi = Developer.builder()
//                 .name("Hadi").build();
//         Developer priyanshi = Developer.builder()
//                 .name("Priyanshi").build();

//         Company bounteous = Company.builder().companyName("Bounteous").build();
//         Company morgan = Company.builder().companyName("Morgan Stanley").build();

//         LearningResource springResource = LearningResource.builder().description("Spring Boot debugging").build();
//         LearningResource hibernate = LearningResource.builder().description("Hibernate").build();

//         Bug freeMoneyMachine = Bug.builder().description("Free Money Machine").build();
//         Bug strategyLeak = Bug.builder().description("Strategy Leak").build();
//         Bug springBootVulnerability = Bug.builder().description("Spring Boot Vulnerability").build();

//         TechStack springBoot = TechStack.builder().name("Spring Boot").build();
//         TechStack fortran = TechStack.builder().name("Fortran").build();
//         TechStack python = TechStack.builder().name("Python").build();

//         BugClaim imadfmm = new BugClaim();
//         BugClaim imadsl = new BugClaim();
//         BugClaim imadsbv = new BugClaim();
//         BugClaim ericsbv = new BugClaim();
//         BugClaim hadisl = new BugClaim();
//         BugClaim hadisbv = new BugClaim();
//         BugClaim priyanshifmm = new BugClaim();

//         Solution ericsbvSol = Solution.builder().description("Stop using Spring Boot!").build();
//         Solution hadislSol = Solution.builder().description("Just don't").build();
//         Solution priyanshifmmSol = Solution.builder().description("Turn off the machine").build();

//         Feedback bte1 = Feedback.builder().feedbackText("No :(").build();
//         Feedback bte2 = Feedback.builder().feedbackText("Ok maybe we will").build();
//         Feedback mtp = Feedback.builder().feedbackText("Oh").build();

//         // relationships
//         //      Company - Resource
//         springResource.setPublisher(bounteous);
//         hibernate.setPublisher(bounteous);
//         bounteous.setResources(List.of(springResource, hibernate));

//         //      TechStack - Bug
//         springBoot.setBugs(List.of(springBootVulnerability));
//         fortran.setBugs(List.of(freeMoneyMachine));
//         python.setBugs(List.of(freeMoneyMachine));
//         springBootVulnerability.setStack(List.of(springBoot));
//         freeMoneyMachine.setStack(List.of(fortran, python));

//         //      Company - Bug
//         springBootVulnerability.setPublisher(bounteous);
//         freeMoneyMachine.setPublisher(morgan);
//         strategyLeak.setPublisher(morgan);
//         bounteous.setBugs(List.of(springBootVulnerability));
//         morgan.setBugs(List.of(freeMoneyMachine, strategyLeak));

//         //      Developer - Claim - Bug
//         imadfmm.setDeveloper(imad);
//         imadfmm.setBug(freeMoneyMachine);
//         imadsbv.setDeveloper(imad);
//         imadsbv.setBug(springBootVulnerability);
//         imadsl.setDeveloper(imad);
//         imadsl.setBug(strategyLeak);
//         imad.setBugClaims(List.of(imadfmm, imadsbv, imadsl));

//         ericsbv.setDeveloper(eric);
//         ericsbv.setBug(springBootVulnerability);
//         eric.setBugClaims(List.of(ericsbv));

//         hadisl.setDeveloper(hadi);
//         hadisl.setBug(strategyLeak);
//         hadisbv.setDeveloper(hadi);
//         hadisbv.setBug(springBootVulnerability);
//         hadi.setBugClaims(List.of(hadisl, hadisbv));

//         priyanshifmm.setDeveloper(priyanshi);
//         priyanshifmm.setBug(freeMoneyMachine);
//         priyanshi.setBugClaims(List.of(priyanshifmm));

//         freeMoneyMachine.setBugClaims(List.of(imadfmm, priyanshifmm));
//         springBootVulnerability.setBugClaims(List.of(hadisbv,ericsbv,imadsbv));
//         strategyLeak.setBugClaims(List.of(hadisl, imadsl));

//         //      Developer - Sol - Bug
//         ericsbvSol.setDeveloper(eric);
//         ericsbvSol.setBug(springBootVulnerability);
//         eric.setSolutions(List.of(ericsbvSol));
//         hadislSol.setDeveloper(hadi);
//         hadislSol.setBug(strategyLeak);
//         hadi.setSolutions(List.of(hadislSol));
//         priyanshifmmSol.setDeveloper(priyanshi);
//         priyanshifmmSol.setBug(freeMoneyMachine);
//         priyanshi.setSolutions(List.of(priyanshifmmSol));

//         springBootVulnerability.setSolutions(List.of(ericsbvSol));
//         freeMoneyMachine.setSolutions(List.of(priyanshifmmSol));
//         strategyLeak.setSolutions(List.of(hadislSol));

//         //      Company - Feedback - Sol
//         bte1.setSolution(ericsbvSol);
//         bte1.setCompany(bounteous);
//         bte2.setSolution(ericsbvSol);
//         bte2.setCompany(bounteous);
//         ericsbvSol.setFeedbacks(List.of(bte1, bte2));
//         mtp.setSolution(priyanshifmmSol);
//         mtp.setCompany(morgan);
//         priyanshifmmSol.setFeedbacks(List.of(mtp));
//         bounteous.setFeedbacks(List.of(bte1, bte2));
//         morgan.setFeedbacks(List.of(mtp));

//         // save
//         developerRepository.saveAll(List.of(imad, eric, hadi, priyanshi));
//         companyRepository.saveAll(List.of(bounteous, morgan));
//         learningResourceRepository.saveAll(List.of(springResource, hibernate));
//         bugRepository.saveAll(List.of(freeMoneyMachine, strategyLeak, springBootVulnerability));
//         techStackRepository.saveAll(List.of(springBoot, fortran, python));
//         bugClaimRepository.saveAll(List.of(imadfmm, imadsl,imadsbv, ericsbv, hadisbv, hadisl, priyanshifmm));
//         solutionRepository.saveAll(List.of(ericsbvSol, hadislSol, priyanshifmmSol));
//         feedbackRepository.saveAll(List.of(bte1, bte2, mtp));
//     }

//     @Test
//     public void checkDevs() {
//         List<Developer> devs = developerRepository.findAll();
//         Assertions.assertEquals(4, devs.size());
//     }

//     @Test
//     public void checkCompanies() {
//         List<Company> companies = companyRepository.findAll();
//         Assertions.assertEquals(2, companies.size());
//     }

//     @Test
//     public void checkLearningResources() {
//         List<LearningResource> resources = learningResourceRepository.findAll();
//         Assertions.assertEquals(2, resources.size());
//     }

//     @Test
//     public void checkBugs() {
//         List<Bug> bugs = bugRepository.findAll();
//         Assertions.assertEquals(3, bugs.size());
//     }

//     @Test
//     public void checkTechStacks() {
//         List<TechStack> stacks = techStackRepository.findAll();
//         Assertions.assertEquals(3, stacks.size());
//     }

//     @Test
//     public void checkClaims() {
//         List<BugClaim> claims = bugClaimRepository.findAll();
//         Assertions.assertEquals(7, claims.size());
//     }

//     @Test
//     public void checkSolutions() {
//         List<Solution> solutions = solutionRepository.findAll();
//         Assertions.assertEquals(3, solutions.size());
//     }

//     @Test
//     public void checkFeedback() {
//         List<Feedback> feedbacks = feedbackRepository.findAll();
//         Assertions.assertEquals(3, feedbacks.size());
//     }

//     @Test
//     public void checkCompanyOfBug() {
//         bugRepository.findByDescription("Spring Boot Vulnerability").ifPresentOrElse(
//                 bug -> {
//                     Company company = bug.getPublisher();
//                     System.out.println(company);
//                     Assertions.assertNotNull(company);
//                     },
//                 ( ) -> {Assertions.fail("Couldn't get bug");}
//         );
//     }

//     @Test
//     public void checkBugsByCompany() {
//         companyRepository.findByCompanyName("Bounteous")
//                 .ifPresentOrElse(
//                         company1 -> {
//                             List<Bug> bugs = company1.getBugs();
//                             Assertions.assertEquals(1, bugs.size());
//                         },
//                         () -> {Assertions.fail("Couldn't get Bounteous bugs");});
//     }
// }
