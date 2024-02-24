package com.tripweaver.services.contracts;

import com.tripweaver.models.FeedbackForDriver;
import com.tripweaver.models.FeedbackForPassenger;

public interface FeedbackService {

    /*ToDo Plamen*/
    FeedbackForDriver createFeedbackForDriver(FeedbackForDriver feedbackForDriver);

    /*ToDo Yuli - DONE*/
    FeedbackForPassenger createFeedbackForPassenger(FeedbackForPassenger FeedbackForPassenger);

    /*Ilia*/
    FeedbackForDriver getFeedbackForDriverById (int id);
    /*Ilia*/
    FeedbackForPassenger getFeedbackForPassengerById (int id);




}
