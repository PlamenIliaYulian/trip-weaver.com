package com.tripweaver.controllers.helpers;

import com.tripweaver.controllers.helpers.contracts.ModelsMapper;
import com.tripweaver.exceptions.InvalidOperationException;
import com.tripweaver.models.Feedback;
import com.tripweaver.models.Travel;
import com.tripweaver.models.User;
import com.tripweaver.models.dtos.*;
import com.tripweaver.models.enums.FeedbackType;
import com.tripweaver.models.filterOptions.TravelFilterOptions;
import com.tripweaver.models.filterOptions.UserFilterOptions;
import com.tripweaver.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.tripweaver.services.helpers.ConstantHelper.CONFIRM_PASSWORD_SHOULD_MATCH_PASSWORD;

@Component
public class ModelsMapperImpl implements ModelsMapper {
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
        if (dto.getEndingPointAddress() == null || dto.getEndingPointAddress().isEmpty()) {
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
    public User userFromDto(UserDto userDto, int userId) {
        User user = userService.getUserById(userId);
        if (!userDto.getConfirmPassword().equals(userDto.getPassword())) {
            throw new InvalidOperationException(CONFIRM_PASSWORD_SHOULD_MATCH_PASSWORD);
        }
        user.setPassword(userDto.getPassword());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPhoneNumber(userDto.getPhoneNumber());
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
    public TravelFilterOptions travelFilterOptionsFromDto(TravelFilterOptionsDto dto) {
        return new TravelFilterOptions(
                dto.getStartingPointCity(),
                dto.getEndingPointCity(),
                dto.getDepartureBefore(),
                dto.getDepartureAfter(),
                dto.getMinFreeSeats(),
                dto.getDriverUsername(),
                dto.getCommentContains(),
                dto.getStatusId(),
                dto.getSortBy(),
                dto.getSortOrder()
        );
    }

    @Override
    public TravelFilterOptions travelFilterOptionsFromSingleUserDto(SingleUserTravelFilterOptionsDto dto) {
        TravelFilterOptions filterOptions = travelFilterOptionsFromDto(dto);
        if (dto.getDriverId() != null) {
            filterOptions.setDriverId(Optional.of(dto.getDriverId()));
        }
        if (dto.getPassengerId() != null) {
            filterOptions.setPassengerId(Optional.of(dto.getPassengerId()));
        }
        return filterOptions;
    }

    @Override
    public UserFilterOptions userFilterOptionsFromDto(UserFilterOptionsDto dto) {
        return new UserFilterOptions(
                dto.getUsername(),
                dto.getEmail(),
                dto.getPhoneNumber(),
                dto.getSortBy(),
                dto.getSortOrder()
        );
    }

    @Override
    public UserDto userDtoFromUser(User userById) {
        return new UserDto(
                userById.getPassword(),
                userById.getPassword(),
                userById.getFirstName(),
                userById.getLastName(),
                userById.getEmail(),
                userById.getPhoneNumber()
        );
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
