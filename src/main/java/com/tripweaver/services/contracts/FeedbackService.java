package com.tripweaver.services.contracts;

import com.tripweaver.models.Feedback;
import com.tripweaver.models.FeedbackForDriver;
import com.tripweaver.models.FeedbackForPassenger;

public interface FeedbackService {

    Feedback createFeedback (Feedback feedback);

    /*Plamen*/
    FeedbackForDriver createFeedbackForDriver(FeedbackForDriver feedbackForDriver);

    /*Yuli - DONE*/
    FeedbackForPassenger createFeedbackForPassenger(FeedbackForPassenger FeedbackForPassenger);

    /*Ilia*/
    FeedbackForDriver getFeedbackForDriverById (int id);
    /*Ilia*/
    FeedbackForPassenger getFeedbackForPassengerById (int id);




}
