package com.tripweaver.repositories.contracts;

import com.tripweaver.models.Feedback;

public interface FeedbackRepository {

    Feedback getFeedbackForDriverById (int id);
    Feedback getFeedbackForPassengerById (int id);
    Feedback createFeedback(Feedback feedback);
}
