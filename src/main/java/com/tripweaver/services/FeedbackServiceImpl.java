package com.tripweaver.services;

import com.tripweaver.models.Feedback;
import com.tripweaver.repositories.contracts.FeedbackRepository;
import com.tripweaver.services.contracts.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;

    @Autowired
    public FeedbackServiceImpl(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }


    @Override
    public Feedback createFeedback(Feedback feedback) {
        feedback.setCreated(LocalDateTime.now());
        return feedbackRepository.createFeedback(feedback);
    }

    /*Ilia*/
    @Override
    public Feedback getFeedbackForDriverById(int id) {
        return feedbackRepository.getFeedbackForDriverById(id);
    }

    /*Ilia*/
    @Override
    public Feedback getFeedbackForPassengerById(int id) {
        return feedbackRepository.getFeedbackForPassengerById(id);
    }

    @Override
    public long getAllFiveStarReviewsCount() {
        return feedbackRepository.getAllFiveStarReviewsCount();
    }
}
