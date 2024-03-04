package com.tripweaver.services.helpers;

import com.tripweaver.exceptions.EntityNotFoundException;
import com.tripweaver.exceptions.InvalidOperationException;
import com.tripweaver.exceptions.UnauthorizedOperationException;
import com.tripweaver.models.Travel;
import com.tripweaver.models.User;

public class ValidationHelpersPlamen {

    public static final int TRAVEL_STATUS_CREATED_ID = 1;
    public static final int COMPLETED_STATUS = 3;

    public static final int ADMIN_ID = 1;

    public static void isUserTheDriver(Travel travel, User userToBeChecked, String message) {
        if (!travel.getDriver().equals(userToBeChecked)) {
            throw new UnauthorizedOperationException(message);
        }
    }
    public static void isTravelOpenForApplication(Travel travelToApplyFor, String message) {
        if (travelToApplyFor.getStatus().getTravelStatusId() != TRAVEL_STATUS_CREATED_ID) {
            throw new UnauthorizedOperationException(message);
        }
    }

    public static void isSameUser(User userToBeUpdated,
                           User loggedUser,
                           String message) {
        if (!userToBeUpdated.equals(loggedUser)) {
            throw new UnauthorizedOperationException(message);
        }
    }

    public static void isUserVerified(User userToBeChecked, String message) {
        if (!userToBeChecked.isVerified()) {
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

    public static void checkNotToBeDriver(Travel travel, User loggedUser,String message){
        if(travel.getDriver().equals(loggedUser)){
            throw new InvalidOperationException(message);
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
        if (travel.getStatus().getTravelStatusId() != COMPLETED_STATUS) {
            throw new InvalidOperationException(message);
        }
    }

    public static void isTheUserInTheApprovedListOfTheTravel(User userToBeChecked, Travel travel, String message) {
        if (!travel.getUsersApprovedForTheTravel().contains(userToBeChecked)) {
            throw new EntityNotFoundException(message);
        }
    }


}
