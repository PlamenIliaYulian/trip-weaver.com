package com.tripweaver.repositories.contracts;

import com.tripweaver.models.FeedbackForDriver;
import com.tripweaver.models.FeedbackForPassenger;

public interface FeedbackRepository {

    FeedbackForDriver createFeedbackForDriver(FeedbackForDriver feedbackForDriver);
    FeedbackForPassenger createFeedbackForPassenger(FeedbackForPassenger FeedbackForPassenger);


}