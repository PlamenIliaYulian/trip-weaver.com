package com.tripweaver.repositories;

import com.tripweaver.models.FeedbackForDriver;
import com.tripweaver.models.FeedbackForPassenger;
import com.tripweaver.repositories.contracts.FeedbackRepository;
import org.springframework.stereotype.Repository;

@Repository
public class FeedbackRepositoryImpl implements FeedbackRepository {
    @Override
    public FeedbackForDriver createFeedbackForDriver(FeedbackForDriver feedbackForDriver) {
        return null;
    }

    @Override
    public FeedbackForPassenger createFeedbackForPassenger(FeedbackForPassenger FeedbackForPassenger) {
        return null;
    }
}
