package com.tripweaver.controllers.helpers.contracts;

import com.tripweaver.models.FeedbackForDriver;
import com.tripweaver.models.FeedbackForPassenger;
import com.tripweaver.models.Travel;
import com.tripweaver.models.User;
import com.tripweaver.models.dtos.*;

public interface ModelsMapper {
    User userFromDtoCreate(UserDtoCreate userDto);

    FeedbackForDriver feedbackForDriverFromDto(FeedbackDto feedbackDto);

    Travel travelFromDto(TravelDto travelDto);

    User userFromDtoUpdate(UserDtoUpdate userDtoUpdate, int userId);

    FeedbackForPassenger feedbackForPassengerFromDto(FeedbackDto feedbackDto);
}
