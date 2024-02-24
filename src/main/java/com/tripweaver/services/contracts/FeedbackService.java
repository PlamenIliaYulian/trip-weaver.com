package com.tripweaver.services.contracts;

import com.tripweaver.models.FeedbackForDriver;
import com.tripweaver.models.FeedbackForPassenger;

public interface FeedbackService {

    FeedbackForDriver createFeedbackForDriver(FeedbackForDriver feedbackForDriver);
    FeedbackForPassenger createFeedbackForPassenger(FeedbackForPassenger FeedbackForPassenger);


}
