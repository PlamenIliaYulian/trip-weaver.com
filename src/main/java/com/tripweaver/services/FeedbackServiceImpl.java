package com.tripweaver.services;

import com.tripweaver.models.FeedbackForDriver;
import com.tripweaver.models.FeedbackForPassenger;
import com.tripweaver.services.contracts.FeedbackService;
import org.springframework.stereotype.Service;

@Service
public class FeedbackServiceImpl implements FeedbackService {
    @Override
    public FeedbackForDriver createFeedbackForDriver(FeedbackForDriver feedbackForDriver) {
        return null;
    }

    @Override
    public FeedbackForPassenger createFeedbackForPassenger(FeedbackForPassenger FeedbackForPassenger) {
        return null;
    }
}
