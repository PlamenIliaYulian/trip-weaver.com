package com.tripweaver.controllers.helpers.contracts;

import com.tripweaver.models.*;
import com.tripweaver.models.dtos.*;
import com.tripweaver.models.filterOptions.TravelFilterOptions;

public interface ModelsMapper {
    User userFromDtoCreate(UserDtoCreate userDto);

    Feedback feedbackForDriverFromDto(FeedbackDto feedbackDto);

    Travel travelFromDto(TravelDto travelDto);

    User userFromDto(UserDto userDto, int userId);

    Feedback feedbackForPassengerFromDto(FeedbackDto feedbackDto);
    TravelFilterOptions travelFilterOptionsFromDto(TravelFilterOptionsDto travelFilterOptionsDto);
}
