package com.tripweaver.services.helpers;

import com.tripweaver.exceptions.DuplicateEntityException;
import com.tripweaver.exceptions.EntityNotFoundException;
import com.tripweaver.exceptions.UnauthorizedOperationException;
import com.tripweaver.models.User;
import com.tripweaver.repositories.contracts.UserRepository;
import com.tripweaver.services.contracts.UserService;
import org.springframework.stereotype.Component;

@Component
public class PermissionHelper {
    public static final int ADMIN_ID = 1;
    private final UserService userService;

    public PermissionHelper(UserService userService) {
        this.userService = userService;
    }

    public void isAdminOrSameUser(User userToBeUpdated,
                                         User loggedUser,
                                         String message) {
        isSameUser(userToBeUpdated, loggedUser, message);
        isAdmin(loggedUser, message);
    }

    public void isSameUser(User userToBeUpdated,
                                  User loggedUser,
                                  String message){
        if(!userToBeUpdated.equals(loggedUser)){
            throw new UnauthorizedOperationException(message);
        }
    }

    public void isAdmin(User loggedInUser, String message) {
        if (loggedInUser.getRoles()
                .stream()
                .noneMatch(role -> role.getRoleId() == ADMIN_ID)) {
            throw new UnauthorizedOperationException(message);
        }
    }

    public void checkForUniqueUsername(User user) {
        boolean duplicateExists = true;

        try {
            userService.getUserByUsername(user.getUsername());

        } catch (EntityNotFoundException e) {
            duplicateExists = false;
        }

        if (duplicateExists) {
            throw new DuplicateEntityException("User", "username", user.getUsername());
        }
    }

    public void checkForUniqueEmail(User user) {
        boolean duplicateExists = true;

        try {
            User existingUser = userService.getUserByEmail(user.getEmail());
            if (existingUser.getUserId() == user.getUserId()) {
                duplicateExists = false;
            }

        } catch (EntityNotFoundException e) {
            duplicateExists = false;
        }

        if (duplicateExists) {
            throw new DuplicateEntityException("User", "email", user.getEmail());
        }
    }

    public void checkIfPhoneNumberUnique(User userToBeUpdated) {
        boolean duplicateExists = true;

        try {
            User user = userService.getUserByPhoneNumber(userToBeUpdated.getPhoneNumber());
            if (user.getUserId() == userToBeUpdated.getUserId()) {
                duplicateExists = false;
            }
        } catch (EntityNotFoundException e) {
            duplicateExists = false;
        }

        if (duplicateExists) {
            throw new DuplicateEntityException("User", "Phone number", userToBeUpdated.getPhoneNumber());
        }
    }


}
