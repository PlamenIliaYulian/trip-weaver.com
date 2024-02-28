package com.tripweaver.controllers.helpers.contracts;

import com.tripweaver.models.*;
import com.tripweaver.models.dtos.*;

public interface ModelsMapper {
    User userFromDtoCreate(UserDtoCreate userDto);

    Feedback feedbackForDriverFromDto(FeedbackDto feedbackDto);

    Travel travelFromDto(TravelDto travelDto);

    User userFromDtoUpdate(UserDtoUpdate userDtoUpdate, int userId);

    Feedback feedbackForPassengerFromDto(FeedbackDto feedbackDto);
}
