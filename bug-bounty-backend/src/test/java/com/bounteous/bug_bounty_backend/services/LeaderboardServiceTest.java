package com.bounteous.bug_bounty_backend.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.bounteous.bug_bounty_backend.data.dto.responses.leaderboard.LeaderboardResponse;
import com.bounteous.bug_bounty_backend.data.entities.humans.Developer;
import com.bounteous.bug_bounty_backend.data.entities.humans.User;
import com.bounteous.bug_bounty_backend.data.repositories.humans.DeveloperRepository;

@ExtendWith(MockitoExtension.class)
class LeaderboardServiceTest {

    @Mock
    private DeveloperRepository developerRepository;

    @Mock
    private UserService userService;

    @Mock
    private RatingService ratingService;

    @InjectMocks
    private LeaderboardService leaderboardService;

    @Test
    void testGetLeaderBoardInfo_whenUserIsDeveloper() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");

        Developer dev1 = Developer.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .password("password123") // required by UserDetails
                .role("DEVELOPER")
                .username("johndoe")
                .rating(4.5f)
                .points(100)
                .build();
        Developer dev2 = Developer.builder()
                .id(2L)
                .firstName("Jane")
                .lastName("Smith")
                .email("jane@example.com")
                .password("password123")
                .role("DEVELOPER")
                .username("janesmith")
                .rating(4.2f)
                .points(90)
                .build();
        List<Developer> topDevs = List.of(dev1, dev2);

        when(developerRepository.findTopDevelopers(PageRequest.of(0, 2))).thenReturn(topDevs);
        when(ratingService.getAverageRatingAsFloat(1L)).thenReturn(4.5f);
        when(ratingService.getAverageRatingAsFloat(2L)).thenReturn(4.2f);
        when(userService.getClaimedBugsByDeveloper(dev1)).thenReturn(List.of());
        when(userService.getClaimedBugsByDeveloper(dev2)).thenReturn(List.of());

        when(developerRepository.existsById(1L)).thenReturn(true);
        when(developerRepository.findTopDevelopers(Pageable.unpaged())).thenReturn(List.of(dev1, dev2));
        when(developerRepository.findById(1L)).thenReturn(Optional.of(dev1));

        // Act
        LeaderboardResponse response = leaderboardService.getLeaderBoardInfo(2, user);

        // Assert
        assertEquals(2, response.getDevelopers().size());
        assertEquals(1, response.getMyRank());
        assertEquals(4.5f, response.getMyRating());
        assertEquals(100, response.getMyPoints());
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
    }

    @Test
    void testGetLeaderBoardInfo_whenUserIsNotDeveloper() {
        // Arrange
        User user = new User();
        user.setId(99L);
        user.setFirstName("Not");
        user.setLastName("Developer");

        Developer dev1 = Developer.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .password("password123")
                .role("DEVELOPER")
                .username("johndoe")
                .rating(4.5f)
                .points(100)
                .build();
        List<Developer> topDevs = List.of(dev1);

        when(developerRepository.findTopDevelopers(PageRequest.of(0, 1))).thenReturn(topDevs);
        when(ratingService.getAverageRatingAsFloat(1L)).thenReturn(4.5f);
        when(userService.getClaimedBugsByDeveloper(dev1)).thenReturn(List.of());

        when(developerRepository.existsById(99L)).thenReturn(false);

        // Act
        LeaderboardResponse response = leaderboardService.getLeaderBoardInfo(1, user);

        // Assert
        assertEquals(1, response.getDevelopers().size());
        assertEquals(-1, response.getMyRank());
        assertEquals(-1f, response.getMyRating());
        assertEquals(-1, response.getMyPoints());
        assertEquals("", response.getFirstName());
        assertEquals("", response.getLastName());
    }
}
