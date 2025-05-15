package com.bounteous.bug_bounty_backend.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bounteous.bug_bounty_backend.data.repositories.bugs.FeedbackRepository;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @Mock
    private FeedbackRepository feedbackRepository;

    @InjectMocks
    private RatingService ratingService;

    @Test
    void testGetAverageRatingAsFloat_returnsCorrectValue() {
        // Arrange
        Long devId = 1L;
        when(feedbackRepository.findAverageRatingByDeveloperId(devId)).thenReturn(4.5);

        // Act
        Float result = ratingService.getAverageRatingAsFloat(devId);

        // Assert
        assertEquals(4.5f, result);
    }

    @Test
    void testGetAverageRatingAsFloat_returnsZeroWhenNull() {
        // Arrange
        Long devId = 2L;
        when(feedbackRepository.findAverageRatingByDeveloperId(devId)).thenReturn(null);

        // Act
        Float result = ratingService.getAverageRatingAsFloat(devId);

        // Assert
        assertEquals(0.0f, result);
    }
}
