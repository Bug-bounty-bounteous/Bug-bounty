package com.bounteous.bug_bounty_backend.services;

import org.springframework.stereotype.Service;

import com.bounteous.bug_bounty_backend.data.repositories.bugs.FeedbackRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final FeedbackRepository feedbackRepository;

    @Transactional(readOnly = true)
    public Float getAverageRatingAsFloat(Long developerId) {
        Double average = feedbackRepository.findAverageRatingByDeveloperId(developerId);
        return average == null ? 0.0f : average.floatValue();
    }
}
