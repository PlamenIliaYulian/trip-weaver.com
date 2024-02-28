package com.tripweaver.services.contracts;

import com.tripweaver.models.Feedback;

public interface FeedbackService {

    Feedback createFeedback (Feedback feedback);
    /*Ilia*/
    Feedback getFeedbackForDriverById (int id);
    /*Ilia*/
    Feedback getFeedbackForPassengerById (int id);




}
