package com.tripweaver.services.helpers;

import com.tripweaver.exceptions.DuplicateEntityException;
import com.tripweaver.exceptions.EntityNotFoundException;
import com.tripweaver.exceptions.InvalidOperationException;
import com.tripweaver.exceptions.UnauthorizedOperationException;
import com.tripweaver.models.Travel;
import com.tripweaver.models.User;
import com.tripweaver.repositories.contracts.UserRepository;
import com.tripweaver.services.contracts.TravelStatusService;
import com.tripweaver.services.contracts.UserService;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class PermissionHelper {
    public static final int ADMIN_ID = 1;
    public static final int TRAVEL_STATUS_CREATED_ID = 1;
    private final UserRepository userRepository;

    private final TravelStatusService travelStatusService;

    public PermissionHelper(UserRepository userRepository, TravelStatusService travelStatusService) {
        this.userRepository = userRepository;
        this.travelStatusService = travelStatusService;
    }

    public void isAdminOrSameUser(User userToBeUpdated,
                                  User loggedUser,
                                  String message) {
        isSameUser(userToBeUpdated, loggedUser, message);
        isAdmin(loggedUser, message);
    }

    public void isSameUser(User userToBeUpdated,
                           User loggedUser,
                           String message) {
        if (!userToBeUpdated.equals(loggedUser)) {
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
            userRepository.getUserByUsername(user.getUsername());

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
            User existingUser = userRepository.getUserByEmail(user.getEmail());
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
            User user = userRepository.getUserByPhoneNumber(userToBeUpdated.getPhoneNumber());
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

    public void isUserTheDriver(Travel travel, User userToBeChecked, String message) {
        if (!travel.getDriver().equals(userToBeChecked)) {
            throw new UnauthorizedOperationException(message);
        }
    }

    public void isUserBlocked(User userToBeChecked, String message) {
        if (userToBeChecked.isBlocked()) {
            throw new UnauthorizedOperationException(message);
        }
    }

    public void isUserVerified(User userToBeChecked, String message) {
        if (!userToBeChecked.isVerified()) {
            throw new UnauthorizedOperationException(message);
        }
    }

    public void hasYetToApply(User userToBeChecked, Travel travelToApplyFor, String message) {
        if (travelToApplyFor.getUsersAppliedForTheTravel().contains(userToBeChecked)) {
            throw new InvalidOperationException(message);
        }
    }

    public void isTravelOpenForApplication(Travel travelToApplyFor, String message) {
        if (!travelToApplyFor.getStatus().equals(travelStatusService.getStatusById(TRAVEL_STATUS_CREATED_ID))) {
            throw new UnauthorizedOperationException(message);
        }
    }

    public void hasAlreadyApplied(User userToBeChecked, Travel travelToApplyFor, String message) {
        if (!travelToApplyFor.getUsersAppliedForTheTravel().contains(userToBeChecked)) {
            throw new InvalidOperationException(message);
        }
    }

    public void hasUserAppliedOrBeingApprovedForTheTravel(User userToBeDeclined,
                                                           Set<User> usersAppliedForTheTravel,
                                                           Set<User> usersApprovedForTheTravel,
                                                           String message) {
        if (!usersAppliedForTheTravel.contains(userToBeDeclined) ||
                !usersApprovedForTheTravel.contains(userToBeDeclined)) {
            throw new EntityNotFoundException(message);
        }
    }


}
