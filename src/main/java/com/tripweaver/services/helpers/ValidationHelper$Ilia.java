package com.tripweaver.services.helpers;

import com.tripweaver.exceptions.EntityNotFoundException;
import com.tripweaver.exceptions.InvalidOperationException;
import com.tripweaver.exceptions.UnauthorizedOperationException;
import com.tripweaver.models.Travel;
import com.tripweaver.models.User;

import java.time.LocalDateTime;
import java.util.Set;

import static com.tripweaver.services.helpers.ConstantHelper.TRAVEL_STATUS_CREATED_ID;

public class ValidationHelper$Ilia {

    public static void isUserBlocked(User userToBeChecked, String message) {
        if (userToBeChecked.isBlocked()) {
            throw new UnauthorizedOperationException(message);
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

    /*ToDo This Should be InvalidOperationException rather than Unauthorized.*/
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
}
