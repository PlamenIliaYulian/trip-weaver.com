package com.tripweaver.controllers.helpers;

import com.tripweaver.controllers.helpers.contracts.ModelsMapper;
import com.tripweaver.exceptions.EntityNotFoundException;
import com.tripweaver.models.FeedbackForDriver;
import com.tripweaver.models.FeedbackForPassenger;
import com.tripweaver.models.Role;
import com.tripweaver.models.User;
import com.tripweaver.models.dtos.FeedbackDto;
import com.tripweaver.models.dtos.UserDto;
import com.tripweaver.services.contracts.AvatarService;
import com.tripweaver.services.contracts.RoleService;
import com.tripweaver.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import com.tripweaver.models.FeedbackForPassenger;
import com.tripweaver.models.Travel;
import com.tripweaver.models.User;
import com.tripweaver.models.dtos.FeedbackDto;
import com.tripweaver.models.dtos.TravelDto;
import com.tripweaver.models.dtos.UserDtoUpdate;
import com.tripweaver.services.contracts.UserService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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

    public User userFromDto(UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPhoneNumber(userDto.getPhoneNumber());
        /*Ilia and Plamen might decide that the part below is not needed. Can be removed, if this is the case.*/
        user.setCreated(LocalDateTime.now());
        user.setDeleted(false);
        user.setBlocked(false);
        user.setAveragePassengerRating(0);
        user.setAverageDriverRating(0);
        user.setAvatar(avatarService.getDefaultAvatar());
        Set<Role> roles = new HashSet<>();
        roles.add(roleService.getRoleById(2));
        user.setRoles(roles);
        Set<FeedbackForDriver> feedbackForDriver = new HashSet<>();
        Set<FeedbackForPassenger> feedbackForPassenger = new HashSet<>();
        user.setFeedbackForDriver(feedbackForDriver);
        user.setFeedbackForPassenger(feedbackForPassenger);
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
