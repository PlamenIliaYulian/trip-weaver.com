package com.tripweaver.services.helpers;

import com.tripweaver.exceptions.EntityNotFoundException;
import com.tripweaver.exceptions.InvalidOperationException;
import com.tripweaver.exceptions.UnauthorizedOperationException;
import com.tripweaver.models.Feedback;
import com.tripweaver.models.Travel;
import com.tripweaver.models.User;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import static com.tripweaver.services.helpers.ConstantHelper.*;

public class ValidationHelper {

    public static void hasAlreadyApplied(User userToBeChecked, Travel travelToApplyFor, String message) {
        if (!travelToApplyFor.getUsersAppliedForTheTravel().contains(userToBeChecked)) {
            throw new InvalidOperationException(message);
        }
    }

    public static void isAdminOrSameUser(User userToBeUpdated,
                                         User loggedUser,
                                         String message) {
        if (loggedUser.getRoles().stream().noneMatch(role -> role.getRoleId() == ADMIN_ID) &&
                !loggedUser.equals(userToBeUpdated)) {
            throw new UnauthorizedOperationException(message);
        }
    }

    public static void isUserBlocked(User userToBeChecked, String message) {
        if (userToBeChecked.isBlocked()) {
            throw new UnauthorizedOperationException(message);
        }
    }

    public static void hasYetToApply(User userToBeChecked, Travel travelToApplyFor, String message) {
        if (travelToApplyFor.getUsersAppliedForTheTravel().contains(userToBeChecked)) {
            throw new InvalidOperationException(message);
        }
    }

    public static void isUserVerified(User userToBeChecked, String message) {
        if (!userToBeChecked.isVerified()) {
            throw new UnauthorizedOperationException(message);
        }
    }

    public static void isDepartureTimeBeforeCurrentMoment(Travel travel, String message) {
        if (travel.getDepartureTime().isBefore(LocalDateTime.now())) {
            throw new InvalidOperationException(message);
        }
    }

    public static void isSameUser(User userToBeUpdated,
                                  User loggedUser,
                                  String message) {
        if (!userToBeUpdated.equals(loggedUser)) {
            throw new UnauthorizedOperationException(message);
        }
    }

    public static void isDriverOrSameUser(Travel travel,
                                          User userToBeUpdated,
                                          User loggedUser,
                                          String message) {
        if (!travel.getDriver().equals(loggedUser) && !userToBeUpdated.equals(loggedUser)) {
            throw new UnauthorizedOperationException(message);
        }
    }

    public static void isTravelOpenForApplication(Travel travelToApplyFor, String message) {
        if (travelToApplyFor.getStatus().getTravelStatusId() != TRAVEL_STATUS_CREATED_ID) {
            throw new InvalidOperationException(message);
        }
    }

    public static void hasUserAppliedOrBeingApprovedForTheTravel(User userToBeDeclined,
                                                                 Set<User> usersAppliedForTheTravel,
                                                                 Set<User> usersApprovedForTheTravel,
                                                                 String message) {
        if (!usersAppliedForTheTravel.contains(userToBeDeclined) &&
                !usersApprovedForTheTravel.contains(userToBeDeclined)) {
            throw new EntityNotFoundException(message);
        }
    }

    public static void isAdmin(User loggedInUser, String message) {
        if (loggedInUser.getRoles()
                .stream()
                .noneMatch(role -> role.getRoleId() == ADMIN_ID)) {
            throw new UnauthorizedOperationException(message);

        }
    }

    public static void isTravelCompleted(Travel travel, String message) {
        if (travel.getStatus().getTravelStatusId() != TRAVEL_STATUS_COMPLETED) {
            throw new InvalidOperationException(message);
        }
    }

    public static void isTheUserInTheApprovedListOfTheTravel(User userToBeChecked, Travel travel, String message) {
        if (!travel.getUsersApprovedForTheTravel().contains(userToBeChecked)) {
            throw new EntityNotFoundException(message);
        }
    }

    public static void isUserTheDriver(Travel travel, User userToBeChecked, String message) {
        if (!travel.getDriver().equals(userToBeChecked)) {
            throw new InvalidOperationException(message);
        }
    }

    public static void checkNotToBeDriver(Travel travel, User loggedUser, String message) {
        if (travel.getDriver().equals(loggedUser)) {
            throw new InvalidOperationException(message);
        }
    }

    public static void isThereAnyFreeSeatsInTravel(Travel travel, Set<User> usersApprovedForTheTravel) {
        if (travel.getFreeSeats() <= usersApprovedForTheTravel.size()) {
            throw new InvalidOperationException(NO_FREE_SEATS_ARE_AVAILABLE_MESSAGE);
        }
    }

    public static void hasUserAlreadyLeftFeedbackForThisTravel(Travel travel,
                                                               User loggedUser,
                                                               User receiver,
                                                               Set<Feedback> feedbackForDriverSet) {
        if (!feedbackForDriverSet.stream()
                .filter(feedback -> feedback.getAuthor().equals(loggedUser))
                .filter(feedback -> feedback.getTravel().equals(travel))
                .filter(feedback -> feedback.getReceiver().equals(receiver))
                .collect(Collectors.toList()).isEmpty()) {
            throw new InvalidOperationException(YOU_HAVE_ALREADY_LEFT_FEEDBACK_FOR_THIS_RIDE);
        }
    }
}