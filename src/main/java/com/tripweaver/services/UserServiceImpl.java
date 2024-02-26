package com.tripweaver.services;

import com.tripweaver.exceptions.EntityNotFoundException;
import com.tripweaver.exceptions.InvalidOperationException;
import com.tripweaver.exceptions.UnauthorizedOperationException;
import com.tripweaver.models.*;
import com.tripweaver.exceptions.DuplicateEntityException;
import com.tripweaver.exceptions.EntityNotFoundException;
import com.tripweaver.exceptions.UnauthorizedOperationException;
import com.tripweaver.models.*;
import com.tripweaver.repositories.contracts.UserRepository;
import com.tripweaver.services.contracts.FeedbackService;
import com.tripweaver.services.contracts.AvatarService;
import com.tripweaver.services.contracts.AvatarService;
import com.tripweaver.services.contracts.RoleService;
import com.tripweaver.services.contracts.UserService;
import com.tripweaver.services.helpers.PermissionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    public static final String UNAUTHORIZED_OPERATION_NOT_ADMIN = "Unauthorized operation. User not admin.";
    public static final String UNAUTHORIZED_OPERATION_NOT_DRIVER = "Unauthorized operation. User not driver of the travel.";
    public static final String TRAVEL_NOT_COMPLETED_CANNOT_LEAVE_FEEDBACK = "Travel not completed and cannot leave feedback.";
    public static final String USER_NOT_IN_APPROVED_LIST = "The user is not in the approved list.";
    public static final String UNAUTHORIZED_OPERATION = "Unauthorized operation.";
    public static final int COMPLETED_STATUS = 3;
    public static final int ADMIN_ID = 1;
    private final UserRepository userRepository;
    private final PermissionHelper permissionHelper;
    private final AvatarService avatarService;
    private final RoleService roleService;
    private final FeedbackService feedbackService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           PermissionHelper permissionHelper,
                           AvatarService avatarService,
                           RoleService roleService,
                           FeedbackService feedbackService) {
        this.userRepository = userRepository;
        this.permissionHelper = permissionHelper;
        this.avatarService = avatarService;
        this.roleService = roleService;
        this.feedbackService = feedbackService;
    }

    @Override
    public User createUser(User user) {
        checkForUniqueUsername(user);
        checkForUniqueEmail(user);
        checkIfPhoneNumberUnique(user);
        return userRepository.createUser(user);
    }

    @Override
    public User updateUser(User user) {
        return userRepository.updateUser(user);
    }

    /*Ilia*/
    @Override
    public List<User> getAllUsers(UserFilterOptions userFilterOptions, User loggedInUser) {
        isAdmin(loggedInUser, UNAUTHORIZED_OPERATION_NOT_ADMIN);
        return userRepository.getAllUsers(userFilterOptions);
    }

    private void isAdmin(User loggedInUser, String message) {
        if (loggedInUser.getRoles()
                .stream()
                .noneMatch(role -> role.getRoleId() == ADMIN_ID)) {
            throw new UnauthorizedOperationException(message);
        }
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.getUserByUsername(username);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.getUserByEmail(email);
    }

    /*Ilia*/
    @Override
    public User getUserById(int id) {
        return userRepository.getUserById(id);
    }

    @Override
    public User blockUser(User userToBeBlocked, User loggedUser) {
        if(!loggedUser.getRoles().contains(roleService.getRoleById(1))){
            throw new UnauthorizedOperationException(UNAUTHORIZED_OPERATION);
        }
        userToBeBlocked.setBlocked(true);
        return userRepository.updateUser(userToBeBlocked);
    }

    @Override
    public User unBlockUser(User userToBeUnBlocked) {
        userToBeUnBlocked.setBlocked(false);
        return userRepository.updateUser(userToBeUnBlocked);
    }

    /*Ilia*/
    @Override
    public User getUserByPhoneNumber(String phoneNumber) {
        return userRepository.getUserByPhoneNumber(phoneNumber);
    }

    @Override
    public long getAllUsersCount() {
        return userRepository.getAllUsersCount();
    }

    @Override
    public List<User> getTopTenTravelOrganizersByRating() {
        return userRepository.getTopTenTravelOrganizersByRating();
    }

    /*Ilia*/
    @Override
    public List<User> getTopTenTravelPassengersByRating() {
        return userRepository.getTopTenTravelPassengersByRating();
    }

    @Override
    public User addAvatar(User userToBeUpdated, String avatar, User loggedUser) {
        if(!userToBeUpdated.equals(loggedUser)){
            throw new UnauthorizedOperationException(UNAUTHORIZED_OPERATION);
        }
        Avatar avatarToAdd = new Avatar();
        avatarToAdd.setAvatarUrl(avatar);
        avatarToAdd = avatarService.createAvatar(avatarToAdd);

        userToBeUpdated.setAvatar(avatarToAdd);
        return userRepository.updateUser(userToBeUpdated);
    }

    @Override
    public User deleteAvatar(User userToBeUpdated, User loggedUser) {
        PermissionHelper.isAdminOrSameUser(
                userRepository.getUserById(userToBeUpdated.getUserId()),
                loggedUser, UNAUTHORIZED_OPERATION);
        userToBeUpdated.setAvatar(avatarService.getDefaultAvatar());
        return userRepository.updateUser(userToBeUpdated);
    }

    /*Ilia TODO To carefully discuss the method with the guys.*/
    @Override
    public User leaveFeedbackForDriver(FeedbackForDriver feedbackForDriver,
                                       Travel travel,
                                       User userToReceiveFeedback,
                                       User userToGiveFeedback) {
        isTravelCompleted(travel);
        isUserTheDriver(travel, userToReceiveFeedback,UNAUTHORIZED_OPERATION_NOT_DRIVER);
        isTheUserInTheApprovedListOfTheTravelToGiveFeedback(userToGiveFeedback, travel);

        feedbackForDriver.setDriverReceivedFeedback(userToReceiveFeedback);
        feedbackForDriver.setPassengerProvidedFeedback(userToReceiveFeedback);
        /*TODO TO discuss with the guys globally where to set creation date-and-time.*/
        feedbackForDriver.setCreated(LocalDateTime.now());
        feedbackForDriver = feedbackService.createFeedbackForDriver(feedbackForDriver);

        Set<FeedbackForDriver> feedbackForDriverSet = userToReceiveFeedback.getFeedbackForDriver();
        feedbackForDriverSet.add(feedbackForDriver);
        userToReceiveFeedback.setFeedbackForDriver(feedbackForDriverSet);
        return updateUser(userToReceiveFeedback);
    }

    private void isTheUserInTheApprovedListOfTheTravelToGiveFeedback(User userToBeChecked, Travel travel) {
        if (!travel.getUsersApprovedForTheTravel().contains(userToBeChecked)) {
            throw new EntityNotFoundException(USER_NOT_IN_APPROVED_LIST);
        }
    }

    /*TODO Is this exception appropriate for this case?*/
    private void isTravelCompleted(Travel travel) {
        if (travel.getStatus().getTravelStatusId() != COMPLETED_STATUS) {
            throw new InvalidOperationException(TRAVEL_NOT_COMPLETED_CANNOT_LEAVE_FEEDBACK);
        }
    }

    private void isUserTheDriver(Travel travel, User userToBeChecked, String message) {
        if (!travel.getDriver().equals(userToBeChecked)) {
            throw new UnauthorizedOperationException(message);
        }
    }

    /*TODO not sure if this is the correct implementation*/
    @Override
    public User leaveFeedbackForPassenger(FeedbackForPassenger feedbackForPassenger, User userToReceiveFeedback) {
        userToReceiveFeedback.getFeedbackForPassenger().add(feedbackForPassenger);
        return userRepository.updateUser(userToReceiveFeedback);
    }

    @Override
    public List<FeedbackForDriver> getAllFeedbackForDriver(User user) {
        return null;
    }

    /*Ilia*/
    @Override
    public List<FeedbackForPassenger> getAllFeedbackForPassenger(User user) {
        return user.getFeedbackForPassenger()
                .stream()
                .sorted(Comparator.comparing(FeedbackForPassenger::getCreated))
                .collect(Collectors.toList());
    }

    private void checkForUniqueUsername(User user) {
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

    private void checkForUniqueEmail(User user) {
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
    private void checkIfPhoneNumberUnique(User userToBeUpdated) {
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
}
