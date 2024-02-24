package com.tripweaver.services.contracts;

import com.tripweaver.models.FeedbackForDriver;
import com.tripweaver.models.FeedbackForPassenger;

public interface FeedbackService {

    /*ToDo Plamen*/
    FeedbackForDriver createFeedbackForDriver(FeedbackForDriver feedbackForDriver);

    /*ToDo Yuli*/
    FeedbackForPassenger createFeedbackForPassenger(FeedbackForPassenger FeedbackForPassenger);


}
