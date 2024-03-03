package com.tripweaver.services.helpers;

import com.tripweaver.exceptions.InvalidOperationException;
import com.tripweaver.exceptions.UnauthorizedOperationException;
import com.tripweaver.models.Travel;
import com.tripweaver.models.User;

import java.time.LocalDateTime;

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
}
