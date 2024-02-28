package com.tripweaver.services.contracts;

import com.tripweaver.models.Feedback;

public interface FeedbackService {

    /*Plamen*/
    Feedback createFeedback (Feedback feedback);
    /*Yuli*/
    Feedback getFeedbackForDriverById (int id);
    /*Ilia*/
    Feedback getFeedbackForPassengerById (int id);




}
