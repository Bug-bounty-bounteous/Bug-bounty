package com.bounteous.bug_bounty_backend.services;

import com.bounteous.bug_bounty_backend.data.dto.requests.solution.SolutionRequest;
import com.bounteous.bug_bounty_backend.data.dto.responses.feedback.FeedbackResponse;
import com.bounteous.bug_bounty_backend.data.dto.responses.solution.SolutionResponse;
import com.bounteous.bug_bounty_backend.data.entities.bugs.*;
import com.bounteous.bug_bounty_backend.data.entities.humans.Admin;
import com.bounteous.bug_bounty_backend.data.entities.humans.Company;
import com.bounteous.bug_bounty_backend.data.entities.humans.Developer;
import com.bounteous.bug_bounty_backend.data.repositories.bugs.BugRepository;
import com.bounteous.bug_bounty_backend.data.repositories.bugs.SolutionRepository;
import com.bounteous.bug_bounty_backend.data.repositories.humans.CompanyRepository;
import com.bounteous.bug_bounty_backend.data.repositories.humans.DeveloperRepository;
import com.bounteous.bug_bounty_backend.data.repositories.humans.UserRepository;
import com.bounteous.bug_bounty_backend.exceptions.BadRequestException;
import com.bounteous.bug_bounty_backend.exceptions.ForbiddenException;
import com.bounteous.bug_bounty_backend.exceptions.ResourceNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.mock.web.MockMultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.Blob;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SolutionsServiceTest {

    @Mock
    private BugRepository bugRepository;
    @Mock
    private DeveloperRepository developerRepository;
    @Mock
    private SolutionRepository solutionRepository;
    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SolutionService solutionService;

    private Bug bug;
    private BugClaim bugClaim;
    private Developer developer;
    private MockMultipartFile file;

    @BeforeEach
    void setup() {
        bug = Bug.builder()
                .id(1L)
                .title("Test Bug")
                .bugStatus(BugStatus.CLAIMED)
                .solutions(new ArrayList<>())
                .build();

        developer = Developer.builder()
                .id(1L)
                .email("dev@example.com")
                .solutions(new ArrayList<>())
                .build();

        bugClaim = BugClaim.builder()
                .bug(bug)
                .developer(developer)
                .build();
        bug.setBugClaims(List.of(bugClaim));
        developer.setBugClaims(List.of(bugClaim));

        file = new MockMultipartFile("file", "solution.txt", "text/plain", "sample content".getBytes());

        // Mocking max file size to 1MB
        solutionService.maxFileSizeBytes = 1024 * 1024;
    }

    @Test
    void postSolution_success() throws Exception {
        SolutionRequest request = new SolutionRequest();
        request.setBugId(bug.getId());
        request.setDescription("Fixes the bug");
        request.setFilename("solution.txt");
        request.setFile(file.getName());
        request.setCodeLink("https://github.com");

        when(bugRepository.findById(bug.getId())).thenReturn(Optional.of(bug));
        when(developerRepository.findByEmail(developer.getEmail())).thenReturn(Optional.of(developer));
        when(solutionRepository.save(any(Solution.class))).then(invocation -> {
            Solution sol = invocation.getArgument(0);
            sol.setId(1L);
            return null;
        });

        Long result = solutionService.postSolution(request, developer.getEmail());

        assertNotNull(result);
        verify(solutionRepository, times(1)).save(any(Solution.class));
    }

    @Test
    void postSolution_throwsWhenBugNotFound() throws IOException {
        when(bugRepository.findById(anyLong())).thenReturn(Optional.empty());

        SolutionRequest request = new SolutionRequest();
        request.setBugId(999L);
        request.setFile(new String(file.getBytes()));

        assertThrows(ResourceNotFoundException.class, () -> solutionService.postSolution(request, developer.getEmail()));
    }

    @Test
    void postSolution_throwsWhenNotClaimed() throws IOException {
        bug.setBugStatus(BugStatus.OPEN);
        when(bugRepository.findById(bug.getId())).thenReturn(Optional.of(bug));
        when(developerRepository.findByEmail(developer.getEmail())).thenReturn(Optional.of(developer));

        SolutionRequest request = new SolutionRequest();
        request.setBugId(bug.getId());
        request.setFile(new String(file.getBytes()));

        assertThrows(BadRequestException.class, () -> solutionService.postSolution(request, developer.getEmail()));
    }

    @Test
    void postSolution_throwsWhenFileTooLarge() throws IOException {
        byte[] largeFile = new byte[2 * 1024 * 1024]; // 2MB
        MockMultipartFile bigFile = new MockMultipartFile("file", "bigfile.txt", "text/plain", largeFile);

        SolutionRequest request = new SolutionRequest();
        request.setBugId(bug.getId());
        request.setFile(new String(bigFile.getBytes()));

        when(bugRepository.findById(bug.getId())).thenReturn(Optional.of(bug));
        when(developerRepository.findByEmail(developer.getEmail())).thenReturn(Optional.of(developer));

        assertThrows(ForbiddenException.class, () -> solutionService.postSolution(request, developer.getEmail()));
    }

    @Test
    void getSolutionsByDeveloperId_returnsSolutions() {
        Solution solution = Solution.builder()
                .id(1L)
                .description("desc")
                .submittedAt(LocalDateTime.now())
                .status(SolutionStatus.SUBMITTED)
                .developer(developer)
                .bug(bug)
                .build();

        when(solutionRepository.findByDeveloper_Id(developer.getId())).thenReturn(List.of(solution));

        var result = solutionService.getSolutionsByDeveloperId(developer.getId());
        assertEquals(1, result.size());
        assertEquals("desc", result.get(0).getDescription());
    }

    @Test
    void getSolutionById_success() {
        Solution solution = Solution.builder()
                .id(1L)
                .description("desc")
                .submittedAt(LocalDateTime.now())
                .status(SolutionStatus.SUBMITTED)
                .developer(developer)
                .bug(bug)
                .build();

        when(solutionRepository.findById(1L)).thenReturn(Optional.of(solution));

        var result = solutionService.getSolutionById(1L);
        assertEquals("desc", result.getDescription());
        assertEquals("SUBMITTED", result.getStatus());
    }

    @Test
    void getSolutionFile_success() throws Exception {
        byte[] content = "file content".getBytes();
        Blob blob = new SerialBlob(content);

        Solution solution = Solution.builder()
                .id(1L)
                .file(blob)
                .filename("file.txt")
                .build();

        when(solutionRepository.findById(1L)).thenReturn(Optional.of(solution));

        var result = solutionService.getSolutionFile(1L);

        assertArrayEquals(content, result.a);
        assertEquals("file.txt", result.b);
    }

    @Test
    void companyCanMutateSolution_returnsTrue_whenBugIsOwnedByCompany() {
        Bug companyBug = Bug.builder().id(1L).build();
        Company company = mock(Company.class);
        when(company.getBugs()).thenReturn(List.of(companyBug));

        Bug solutionBug = Bug.builder().id(1L).build();
        Solution solution = Solution.builder().bug(solutionBug).build();

        boolean result = solutionService.companyCanMutateSolution(company, solution);
        assertTrue(result);
    }

    @Test
    void companyCanMutateSolution_returnsFalse_whenBugIsNotOwnedByCompany() {
        Bug companyBug = Bug.builder().id(2L).build();
        Company company = mock(Company.class);
        when(company.getBugs()).thenReturn(List.of(companyBug));

        Bug solutionBug = Bug.builder().id(1L).build();
        Solution solution = Solution.builder().bug(solutionBug).build();

        boolean result = solutionService.companyCanMutateSolution(company, solution);
        assertFalse(result);
    }

    @Test
    void setVerdict_setsAcceptedStatus_andUpdatesBugAndDeveloper() {
        Bug bug = Bug.builder().id(1L).title("Bug 1").reward(50.0).build();
        Developer dev = Developer.builder().points(10).build();
        Solution sol = Solution.builder()
                .status(SolutionStatus.SUBMITTED)
                .bug(bug)
                .developer(dev)
                .build();

        Company company = mock(Company.class);
        when(solutionRepository.findById(1L)).thenReturn(Optional.of(sol));
        when(companyRepository.findByEmail("company@email.com")).thenReturn(Optional.of(company));
        when(company.getBugs()).thenReturn(List.of(bug));

        solutionService.setVerdict(1L, "ACCEPTED", "company@email.com");

        assertEquals(SolutionStatus.ACCEPTED, sol.getStatus());
        assertEquals(BugStatus.RESOLVED, bug.getBugStatus());
        assertEquals(60, dev.getPoints());

        verify(solutionRepository).save(sol);
        verify(bugRepository).save(bug);
        verify(developerRepository).save(dev);
    }

    @Test
    void setVerdict_throwsWhenStatusIsInvalid() {
        Solution sol = Solution.builder()
                .bug(bug)
                .developer(developer)
                .build();

        Company company = mock(Company.class);
        when(solutionRepository.findById(1L)).thenReturn(Optional.of(sol));
        when(companyRepository.findByEmail("comp@email.com")).thenReturn(Optional.of(company));
        when(company.getBugs()).thenReturn(List.of(bug));

        assertThrows(BadRequestException.class, () ->
                solutionService.setVerdict(1L, "INVALID_STATUS", "comp@email.com"));
    }

    @Test
    void setVerdict_throwsWhenCompanyCannotMutate() {
        Solution sol = Solution.builder()
                .bug(bug)
                .developer(developer)
                .build();

        Company company = mock(Company.class);
        when(solutionRepository.findById(1L)).thenReturn(Optional.of(sol));
        when(companyRepository.findByEmail("noaccess@email.com")).thenReturn(Optional.of(company));
        when(company.getBugs()).thenReturn(List.of()); // Doesn't own the bug

        assertThrows(BadRequestException.class, () ->
                solutionService.setVerdict(1L, "REJECTED", "noaccess@email.com"));
    }

    @Test
    void getSolutionsForBug_developerGetsOnlyTheirSolutions() {
        Bug testBug = Bug.builder().id(1L).title("Bug A").solutions(new ArrayList<>()).build();
        Solution solution = Solution.builder()
                .id(10L)
                .developer(developer)
                .bug(testBug)
                .status(SolutionStatus.SUBMITTED)
                .submittedAt(LocalDateTime.now())
                .build();
        testBug.getSolutions().add(solution);

        developer.setId(99L);
        developer.setSolutions(List.of(solution));

        when(userRepository.findByEmail(developer.getEmail())).thenReturn(Optional.of(developer));
        when(bugRepository.findById(1L)).thenReturn(Optional.of(testBug));

        List<SolutionResponse> responses = solutionService.getSolutionsForBug(1L, developer.getEmail());

        assertEquals(1, responses.size());
        assertEquals(10L, responses.get(0).getId());
    }

    @Test
    void getSolutionsForBug_companyGetsAllSolutions() {
        Bug testBug = Bug.builder().id(1L).title("Bug B").solutions(new ArrayList<>()).build();
        Solution solution = Solution.builder()
                .id(20L)
                .developer(developer)
                .bug(testBug)
                .status(SolutionStatus.SUBMITTED)
                .submittedAt(LocalDateTime.now())
                .build();
        testBug.getSolutions().add(solution);

        Company company = mock(Company.class);
        when(company.publishedBug(testBug)).thenReturn(true);

        when(userRepository.findByEmail("comp@b.com")).thenReturn(Optional.of(company));
        when(bugRepository.findById(1L)).thenReturn(Optional.of(testBug));

        List<SolutionResponse> responses = solutionService.getSolutionsForBug(1L, "comp@b.com");

        assertEquals(1, responses.size());
        assertEquals(20L, responses.get(0).getId());
    }

    @Test
    void getSolutionsForBug_returnsEmptyIfUserNotAuthorized() {
        Admin admin = new Admin(); // should return all solutions
        Bug testBug = Bug.builder().id(1L).title("Bug C").solutions(new ArrayList<>()).build();

        Solution solution = Solution.builder()
                .id(30L)
                .developer(developer)
                .bug(testBug)
                .status(SolutionStatus.SUBMITTED)
                .submittedAt(LocalDateTime.now())
                .build();
        testBug.getSolutions().add(solution);

        when(userRepository.findByEmail("admin@admin.com")).thenReturn(Optional.of(admin));
        when(bugRepository.findById(1L)).thenReturn(Optional.of(testBug));

        List<SolutionResponse> responses = solutionService.getSolutionsForBug(1L, "admin@admin.com");

        assertEquals(1, responses.size());
        assertEquals(30L, responses.get(0).getId());
    }

    @Test
    void getSolutionsByCompanyId_returnsMappedResponsesIncludingFeedbacks() {
        Company company = Company.builder().id(1L).email("comp@example.com").companyName("Comp").build();
        Bug bug = Bug.builder().id(10L).title("Bug X").publisher(company).build();

        Developer developer = Developer.builder()
                .id(2L)
                .username("dev")
                .email("dev@x.com")
                .rating(4.0F)
                .build();

        Feedback feedback = Feedback.builder()
                .id(5L)
                .feedbackMessage("Great job")
                .rating(5)
                .submittedAt(LocalDateTime.now())
                .company(company)
                .build();

        Solution solution = Solution.builder()
                .id(100L)
                .description("This is a solution")
                .codeLink("http://github.com/code")
                .status(SolutionStatus.ACCEPTED)
                .submittedAt(LocalDateTime.now().minusDays(1))
                .reviewedAt(LocalDateTime.now())
                .bug(bug)
                .developer(developer)
                .feedbacks(List.of(feedback))
                .build();

        when(solutionRepository.findByBug_Publisher_Id(1L)).thenReturn(List.of(solution));

        List<SolutionResponse> responses = solutionService.getSolutionsByCompanyId(1L);

        assertEquals(1, responses.size());
        SolutionResponse resp = responses.get(0);

        assertEquals(100L, resp.getId());
        assertEquals("This is a solution", resp.getDescription());
        assertEquals("Bug X", resp.getBug().getTitle());
        assertEquals("dev", resp.getDeveloper().getUsername());
        assertEquals(1, resp.getFeedbacks().size());

        FeedbackResponse fbResp = resp.getFeedbacks().get(0);
        assertEquals("Great job", fbResp.getFeedbackMessage());
        assertEquals(5, fbResp.getRating());
        assertEquals("Comp", fbResp.getCompany().getCompanyName());
    }

}
