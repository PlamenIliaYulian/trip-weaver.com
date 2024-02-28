package com.tripweaver.services;

import com.tripweaver.models.Feedback;
import com.tripweaver.models.FeedbackForDriver;
import com.tripweaver.models.FeedbackForPassenger;
import com.tripweaver.repositories.contracts.FeedbackRepository;
import com.tripweaver.services.contracts.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;

    @Autowired
    public FeedbackServiceImpl(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }


    @Override
    public Feedback createFeedback(Feedback feedback) {
        return feedbackRepository.createFeedback(feedback);
    }

    @Override
    public FeedbackForDriver createFeedbackForDriver(FeedbackForDriver feedbackForDriver) {
        return feedbackRepository.createFeedbackForDriver(feedbackForDriver);
    }


    @Override
    public FeedbackForPassenger createFeedbackForPassenger(FeedbackForPassenger feedbackForPassenger) {
        return feedbackRepository.createFeedbackForPassenger(feedbackForPassenger);
    }

    /*Ilia*/
    @Override
    public FeedbackForDriver getFeedbackForDriverById(int id) {
        return feedbackRepository.getFeedbackForDriverById(id);
    }

    /*Ilia*/
    @Override
    public FeedbackForPassenger getFeedbackForPassengerById(int id) {
        return feedbackRepository.getFeedbackForPassengerById(id);
    }
}
