package com.tripweaver.services.helpers;

import com.tripweaver.exceptions.UnauthorizedOperationException;
import com.tripweaver.models.User;

public class ValidationHelper$Ilia {

    public static void isUserBlocked(User userToBeChecked, String message) {
        if (userToBeChecked.isBlocked()) {
            throw new UnauthorizedOperationException(message);
        }
    }
}
