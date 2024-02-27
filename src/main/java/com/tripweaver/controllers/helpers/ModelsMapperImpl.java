package com.tripweaver.controllers.helpers;

import com.tripweaver.controllers.helpers.contracts.ModelsMapper;
import com.tripweaver.models.FeedbackForPassenger;
import com.tripweaver.models.Travel;
import com.tripweaver.models.User;
import com.tripweaver.models.dtos.FeedbackDto;
import com.tripweaver.models.dtos.TravelDto;
import com.tripweaver.models.dtos.UserDtoUpdate;
import com.tripweaver.services.contracts.UserService;
import org.springframework.stereotype.Component;

@Component
public class ModelsMapperImpl implements ModelsMapper {

    private final UserService userService;

    public ModelsMapperImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Travel travelFromDto(TravelDto dto) {
        Travel travel = new Travel();
        travel.setStartingPoint(dto.getStartingPoint());
        travel.setEndingPoint(dto.getEndingPoint());
        travel.setDepartureTime(dto.getDepartureTime());
        travel.setFreeSeats(dto.getFreeSeats());
        travel.setComment(dto.getComment());
        return travel;
    }

    /*ToDo To add a check if the value is not null, then to change fields.*/
    @Override
    public User userFromDtoUpdate(UserDtoUpdate userDtoUpdate, int userId) {
        User user = userService.getUserById(userId);
        user.setPassword(userDtoUpdate.getPassword());
        user.setFirstName(userDtoUpdate.getFirstName());
        user.setLastName(userDtoUpdate.getLastName());
        user.setEmail(userDtoUpdate.getEmail());
        user.setPhoneNumber(userDtoUpdate.getPhoneNumber());
        return user;
    }

    @Override
    public FeedbackForPassenger feedbackForPassengerFromDto(FeedbackDto feedbackDto) {
        FeedbackForPassenger feedbackForPassenger = new FeedbackForPassenger();
        User userReceivingFeedback = userService.getUserById(feedbackDto.getReceiverUserId());
        feedbackForPassenger.setPassengerReceivedFeedback(userReceivingFeedback);
        feedbackForPassenger.setRating(feedbackDto.getRating());
        feedbackForPassenger.setContent(feedbackDto.getContent());
        return feedbackForPassenger;
    }
}
