package com.tripweaver.services;

import com.tripweaver.models.*;
import com.tripweaver.models.enums.FeedbackType;
import com.tripweaver.models.filterOptions.UserFilterOptions;
import com.tripweaver.repositories.contracts.UserRepository;
import com.tripweaver.services.contracts.AvatarService;
import com.tripweaver.services.contracts.FeedbackService;
import com.tripweaver.services.contracts.RoleService;
import com.tripweaver.services.contracts.UserService;
import com.tripweaver.services.helpers.PermissionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    public static final String UNAUTHORIZED_OPERATION_NOT_ADMIN = "Unauthorized operation. User not admin.";
    public static final String UNAUTHORIZED_OPERATION_NOT_DRIVER = "Unauthorized operation. User not driver of the travel.";
    public static final String UNAUTHORIZED_OPERATION_NOT_SAME_USER = "Unauthorized operation. Not same user.";
    public static final String TRAVEL_NOT_COMPLETED_CANNOT_LEAVE_FEEDBACK = "Travel not completed and cannot leave feedback.";
    public static final String USER_NOT_IN_APPROVED_LIST = "The user is not in the approved list.";
    public static final String UNAUTHORIZED_OPERATION = "Unauthorized operation.";
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
        permissionHelper.checkForUniqueUsername(user);
        permissionHelper.checkForUniqueEmail(user);
        permissionHelper.checkIfPhoneNumberUnique(user);
        return userRepository.createUser(user);
    }

    @Override
    public User updateUser(User user, User loggedUser) {
        permissionHelper.isSameUser(user, loggedUser, UNAUTHORIZED_OPERATION);
        permissionHelper.checkForUniqueEmail(user);
        permissionHelper.checkIfPhoneNumberUnique(user);
        return userRepository.updateUser(user);
    }

    /*Ilia*/
    @Override
    public List<User> getAllUsers(UserFilterOptions userFilterOptions, User loggedInUser) {
        permissionHelper.isAdmin(loggedInUser, UNAUTHORIZED_OPERATION_NOT_ADMIN);
        return userRepository.getAllUsers(userFilterOptions);
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
        permissionHelper.isAdmin(loggedUser, UNAUTHORIZED_OPERATION_NOT_ADMIN);
        userToBeBlocked.setBlocked(true);
        return userRepository.updateUser(userToBeBlocked);
    }

    @Override
    public User unBlockUser(User userToBeUnBlocked, User loggedUser) {
        permissionHelper.isAdmin(loggedUser, UNAUTHORIZED_OPERATION_NOT_ADMIN);
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
    public User addAvatar(User userToBeUpdated, String avatarUrl, User loggedUser) {
        permissionHelper.isSameUser(userToBeUpdated, loggedUser, UNAUTHORIZED_OPERATION_NOT_SAME_USER);
        Avatar avatarToAdd = new Avatar();
        avatarToAdd.setAvatarUrl(avatarUrl);
        avatarToAdd = avatarService.createAvatar(avatarToAdd);

        userToBeUpdated.setAvatar(avatarToAdd);
        return userRepository.updateUser(userToBeUpdated);
    }

    @Override
    public User deleteAvatar(User userToBeUpdated, User loggedUser) {
        permissionHelper.isAdminOrSameUser(userToBeUpdated, loggedUser, UNAUTHORIZED_OPERATION);
        userToBeUpdated.setAvatar(avatarService.getDefaultAvatar());
        return userRepository.updateUser(userToBeUpdated);
    }

    /*Ilia*/
    @Override
    public Feedback leaveFeedbackForDriver(Feedback feedbackForDriver,
                                           Travel travel,
                                           User loggedUser,
                                           User driver) {
        permissionHelper.isTravelCompleted(travel, TRAVEL_NOT_COMPLETED_CANNOT_LEAVE_FEEDBACK);
        permissionHelper.isTheUserInTheApprovedListOfTheTravel(loggedUser, travel, USER_NOT_IN_APPROVED_LIST);
        permissionHelper.isUserTheDriver(travel, driver, UNAUTHORIZED_OPERATION_NOT_DRIVER);

        feedbackForDriver.setReceiver(driver);
        feedbackForDriver.setAuthor(loggedUser);
        feedbackForDriver.setTravel(travel);
        feedbackForDriver = feedbackService.createFeedback(feedbackForDriver);
        Set<Feedback> feedbackForDriverSet = driver.getFeedback();
        feedbackForDriverSet.add(feedbackForDriver);

        driver.setAverageDriverRating(feedbackForDriverSet
                .stream()
                .filter(feedback -> feedback.getFeedbackType().equals(FeedbackType.FOR_DRIVER))
                .mapToDouble(Feedback::getRating)
                .average()
                .orElseThrow());
        userRepository.updateUser(driver);
        return feedbackForDriver;
    }


    @Override
    public Feedback leaveFeedbackForPassenger(Feedback feedbackForPassenger,
                                              Travel travel,
                                              User loggedUser,
                                              User passenger) {
        permissionHelper.isTravelCompleted(travel, TRAVEL_NOT_COMPLETED_CANNOT_LEAVE_FEEDBACK);
        permissionHelper.isUserTheDriver(travel, loggedUser, UNAUTHORIZED_OPERATION_NOT_DRIVER);
        permissionHelper.isTheUserInTheApprovedListOfTheTravel(passenger, travel, USER_NOT_IN_APPROVED_LIST);

        feedbackForPassenger.setReceiver(passenger);
        feedbackForPassenger.setAuthor(loggedUser);
        feedbackForPassenger.setTravel(travel);
        feedbackForPassenger = feedbackService.createFeedback(feedbackForPassenger);
        Set<Feedback> feedbackForPassengerSet = passenger.getFeedback();
        feedbackForPassengerSet.add(feedbackForPassenger);

        passenger.setAveragePassengerRating(feedbackForPassengerSet
                .stream()
                .filter(feedback -> feedback.getFeedbackType().equals(FeedbackType.FOR_PASSENGER))
                .mapToDouble(Feedback::getRating)
                .average()
                .orElseThrow());
        userRepository.updateUser(passenger);
        return feedbackForPassenger;
    }

    @Override
    public List<Feedback> getAllFeedbackForDriver(User user) {
        return user.getFeedback()
                .stream()
                .sorted(Comparator.comparing(Feedback::getCreated).reversed())
                .collect(Collectors.toList());
    }

    /*Ilia*/
    @Override
    public List<Feedback> getAllFeedbackForPassenger(User user) {
        return user.getFeedback()
                .stream()
                .sorted(Comparator.comparing(Feedback::getCreated).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public User verifyEmail(User userToBeVerified) {
        userToBeVerified.setVerified(true);
        return userRepository.updateUser(userToBeVerified);
    }


}
