package com.tripweaver.controllers.helpers;

import com.tripweaver.controllers.helpers.contracts.ModelsMapper;
import com.tripweaver.exceptions.EntityNotFoundException;
import com.tripweaver.models.FeedbackForDriver;
import com.tripweaver.models.FeedbackForPassenger;
import com.tripweaver.models.Role;
import com.tripweaver.models.User;
import com.tripweaver.models.dtos.*;
import com.tripweaver.services.contracts.AvatarService;
import com.tripweaver.services.contracts.RoleService;
import com.tripweaver.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import com.tripweaver.models.FeedbackForPassenger;
import com.tripweaver.models.Travel;
import com.tripweaver.models.User;
import com.tripweaver.models.dtos.FeedbackDto;
import com.tripweaver.services.contracts.UserService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Component
public class ModelsMapperImpl implements ModelsMapper {
    private UserService userService;
    private AvatarService avatarService;
    private RoleService roleService;

    @Autowired
    public ModelsMapperImpl(
            UserService userService,
            AvatarService avatarService,
            RoleService roleService) {
        this.userService = userService;
        this.avatarService = avatarService;
        this.roleService = roleService;
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
    public FeedbackForDriver feedbackForDriverFromDto(
            FeedbackDto feedbackDto,
            User passengerProvidingTheFeedback) {
        FeedbackForDriver feedbackForDriver = new FeedbackForDriver();
        feedbackForDriver.setRating(feedbackDto.getRating());
        feedbackForDriver.setContent(feedbackDto.getContent());
        feedbackForDriver.setPassengerProvidedFeedback(passengerProvidingTheFeedback);
        feedbackForDriver.setCreated(LocalDateTime.now());
        User driver = userService.getUserById(feedbackDto.getReceiverUserId());
        feedbackForDriver.setDriverReceivedFeedback(driver);
        return feedbackForDriver;
    }
}
