package com.tripweaver.repositories.contracts;

import com.tripweaver.models.Feedback;
import com.tripweaver.models.FeedbackForDriver;
import com.tripweaver.models.FeedbackForPassenger;

public interface FeedbackRepository {

    FeedbackForDriver createFeedbackForDriver(FeedbackForDriver feedbackForDriver);
    FeedbackForPassenger createFeedbackForPassenger(FeedbackForPassenger FeedbackForPassenger);
    FeedbackForDriver getFeedbackForDriverById (int id);
    FeedbackForPassenger getFeedbackForPassengerById (int id);

    Feedback createFeedback(Feedback feedback);
}
