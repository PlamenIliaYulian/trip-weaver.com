package com.tripweaver.controllers.helpers.contracts;

import com.tripweaver.models.FeedbackForDriver;
import com.tripweaver.models.FeedbackForPassenger;
import com.tripweaver.models.Travel;
import com.tripweaver.models.User;
import com.tripweaver.models.dtos.FeedbackDto;
import com.tripweaver.models.dtos.TravelDto;
import com.tripweaver.models.dtos.UserDtoUpdate;

public interface ModelsMapper {

    Travel travelFromDto (TravelDto travelDto);
    User userFromDtoUpdate (UserDtoUpdate userDtoUpdate, int userId);
    FeedbackForPassenger feedbackForPassengerFromDto (FeedbackDto feedbackDto);
}
