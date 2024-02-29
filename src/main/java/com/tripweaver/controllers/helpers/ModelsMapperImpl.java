package com.tripweaver.controllers.helpers;

import com.tripweaver.controllers.helpers.contracts.ModelsMapper;
import com.tripweaver.exceptions.InvalidOperationException;
import com.tripweaver.exceptions.UnauthorizedOperationException;
import com.tripweaver.models.*;
import com.tripweaver.models.dtos.FeedbackDto;
import com.tripweaver.models.dtos.TravelDto;
import com.tripweaver.models.dtos.UserDtoCreate;
import com.tripweaver.models.dtos.UserDtoUpdate;
import com.tripweaver.models.enums.FeedbackType;
import com.tripweaver.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ModelsMapperImpl implements ModelsMapper {
    public static final String CONFIRM_PASSWORD_SHOULD_MATCH_PASSWORD = "Confirm password should match password";
    private final UserService userService;

    @Autowired
    public ModelsMapperImpl(
            UserService userService) {
        this.userService = userService;

    }

    @Override
    public Travel travelFromDto(TravelDto dto) {
        Travel travel = new Travel();
        travel.setStartingPointCity(dto.getStartingPointCity());
        travel.setStartingPointAddress(dto.getStartingPointAddress());
        travel.setEndingPointCity(dto.getEndingPointCity());
        if (dto.getEndingPointAddress().isEmpty() || dto.getEndingPointAddress() == null) {
            travel.setEndingPointAddress("");
        } else {
            travel.setEndingPointAddress(dto.getEndingPointAddress());
        }
        travel.setDepartureTime(dto.getDepartureTime());
        travel.setFreeSeats(dto.getFreeSeats());
        travel.setComment(dto.getComment());
        return travel;
    }

    @Override
    public User userFromDtoUpdate(UserDtoUpdate userDtoUpdate, int userId) {
        User user = userService.getUserById(userId);
        if(!userDtoUpdate.getConfirmPassword().equals(userDtoUpdate.getPassword())){
            throw new InvalidOperationException(CONFIRM_PASSWORD_SHOULD_MATCH_PASSWORD);
        }
        user.setPassword(userDtoUpdate.getPassword());
        user.setFirstName(userDtoUpdate.getFirstName());
        user.setLastName(userDtoUpdate.getLastName());
        user.setEmail(userDtoUpdate.getEmail());
        user.setPhoneNumber(userDtoUpdate.getPhoneNumber());
        return user;
    }

    @Override
    public Feedback feedbackForPassengerFromDto(FeedbackDto feedbackDto) {
        Feedback feedbackForPassenger = new Feedback();
        feedbackForPassenger.setRating(feedbackDto.getRating());
        feedbackForPassenger.setFeedbackType(FeedbackType.FOR_PASSENGER);
        feedbackForPassenger.setContent(feedbackDto.getContent());
        return feedbackForPassenger;
    }

    @Override
    public User userFromDtoCreate(UserDtoCreate userDtoCreate) {
        User user = new User();
        user.setUsername(userDtoCreate.getUsername());
        user.setPassword(userDtoCreate.getPassword());
        user.setFirstName(userDtoCreate.getFirstName());
        user.setLastName(userDtoCreate.getLastName());
        user.setEmail(userDtoCreate.getEmail());
        user.setPhoneNumber(userDtoCreate.getPhoneNumber());
        return user;
    }

    @Override
    public Feedback feedbackForDriverFromDto(FeedbackDto feedbackDto) {
        Feedback feedbackForDriver = new Feedback();
        feedbackForDriver.setRating(feedbackDto.getRating());
        feedbackForDriver.setFeedbackType(FeedbackType.FOR_DRIVER);
        feedbackForDriver.setContent(feedbackDto.getContent());
        return feedbackForDriver;
    }
}
