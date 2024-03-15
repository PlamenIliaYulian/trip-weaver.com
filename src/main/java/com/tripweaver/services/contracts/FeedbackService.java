package com.tripweaver.services.contracts;

import com.tripweaver.models.Feedback;

public interface FeedbackService {

    Feedback createFeedback(Feedback feedback);

    Feedback getFeedbackForDriverById(int id);

    Feedback getFeedbackForPassengerById(int id);

    long getAllFiveStarReviewsCount();


}
