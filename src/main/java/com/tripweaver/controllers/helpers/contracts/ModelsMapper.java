package com.tripweaver.controllers.helpers.contracts;

import com.tripweaver.models.FeedbackForDriver;
import com.tripweaver.models.FeedbackForPassenger;
import com.tripweaver.models.Travel;
import com.tripweaver.models.User;
import com.tripweaver.models.dtos.FeedbackDto;
import com.tripweaver.models.dtos.TravelDto;
import com.tripweaver.models.dtos.UserDtoUpdate;

import com.tripweaver.models.User;
import com.tripweaver.models.dtos.UserDto;

public interface ModelsMapper {
    User userFromDto(UserDto userDto);

    Travel travelFromDto (TravelDto travelDto);
    User userFromDtoUpdate (UserDtoUpdate userDtoUpdate, int userId);
    FeedbackForPassenger feedbackForPassengerFromDto (FeedbackDto feedbackDto);
}
