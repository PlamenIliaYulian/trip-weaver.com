package com.tripweaver.services;

import com.tripweaver.exceptions.DuplicateEntityException;
import com.tripweaver.exceptions.EntityNotFoundException;
import com.tripweaver.exceptions.InvalidOperationException;
import com.tripweaver.models.*;
import com.tripweaver.models.enums.FeedbackType;
import com.tripweaver.models.filterOptions.UserFilterOptions;
import com.tripweaver.repositories.contracts.UserRepository;
import com.tripweaver.services.contracts.AvatarService;
import com.tripweaver.services.contracts.FeedbackService;
import com.tripweaver.services.contracts.RoleService;
import com.tripweaver.services.contracts.UserService;
import com.tripweaver.services.helpers.ValidationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.tripweaver.services.TravelServiceImpl.INVALID_OPERATION_DRIVER;

@Service
public class UserServiceImpl implements UserService {

    public static final String UNAUTHORIZED_OPERATION_NOT_ADMIN = "Unauthorized operation. User not admin.";
    public static final String UNAUTHORIZED_OPERATION_NOT_DRIVER = "Unauthorized operation. User not driver of the travel.";
    public static final String UNAUTHORIZED_OPERATION_NOT_SAME_USER = "Unauthorized operation. Not same user.";
    public static final String TRAVEL_NOT_COMPLETED_CANNOT_LEAVE_FEEDBACK = "Travel not completed and cannot leave feedback.";
    public static final String USER_NOT_IN_APPROVED_LIST = "The user is not in the approved list.";
    public static final String UNAUTHORIZED_OPERATION = "Unauthorized operation.";
    public static final String YOU_HAVE_ALREADY_LEFT_FEEDBACK_FOR_THIS_RIDE = "You have already left feedback for this ride.";
    private final UserRepository userRepository;
    private final AvatarService avatarService;
    private final RoleService roleService;
    private final FeedbackService feedbackService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           AvatarService avatarService,
                           RoleService roleService,
                           FeedbackService feedbackService) {
        this.userRepository = userRepository;
        this.avatarService = avatarService;
        this.roleService = roleService;
        this.feedbackService = feedbackService;
    }

    @Override
    public User createUser(User user) {
        checkForUniqueUsername(user);
        checkForUniqueEmail(user);
        checkIfPhoneNumberUnique(user);
        user.setCreated(LocalDateTime.now());
        user.setAvatar(avatarService.getDefaultAvatar());
        HashSet<Role> roles = new HashSet<>();
        roles.add(roleService.getRoleById(2));
        user.setRoles(roles);
        return userRepository.createUser(user);
    }

    /*TODO implement deleteUser method*/

    @Override
    public User updateUser(User user, User loggedUser) {
        ValidationHelper.isSameUser(user, loggedUser, UNAUTHORIZED_OPERATION);
        checkForUniqueEmail(user);
        checkIfPhoneNumberUnique(user);
        return userRepository.updateUser(user);
    }

    /*Ilia*/
    @Override
    public List<User> getAllUsers(UserFilterOptions userFilterOptions, User loggedInUser) {
        ValidationHelper.isAdmin(loggedInUser, UNAUTHORIZED_OPERATION_NOT_ADMIN);
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
        ValidationHelper.isAdmin(loggedUser, UNAUTHORIZED_OPERATION_NOT_ADMIN);
        userToBeBlocked.setBlocked(true);
        return userRepository.updateUser(userToBeBlocked);
    }

    @Override
    public User unBlockUser(User userToBeUnBlocked, User loggedUser) {
        ValidationHelper.isAdmin(loggedUser, UNAUTHORIZED_OPERATION_NOT_ADMIN);
        userToBeUnBlocked.setBlocked(false);
        return userRepository.updateUser(userToBeUnBlocked);
    }

    /*Ilia*/
    /*ToDo We make the validation in Helper Class calling directly the repository
    *  so we do not need this method. It is not needed to be tested.*/
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
        ValidationHelper.isSameUser(userToBeUpdated, loggedUser, UNAUTHORIZED_OPERATION_NOT_SAME_USER);
        Avatar avatarToAdd = new Avatar();
        avatarToAdd.setAvatarUrl(avatarUrl);
        avatarToAdd = avatarService.createAvatar(avatarToAdd);

        userToBeUpdated.setAvatar(avatarToAdd);
        return userRepository.updateUser(userToBeUpdated);
    }

    @Override
    public User deleteAvatar(User userToBeUpdated, User loggedUser) {
        ValidationHelper.isAdminOrSameUser(userToBeUpdated, loggedUser, UNAUTHORIZED_OPERATION);
        userToBeUpdated.setAvatar(avatarService.getDefaultAvatar());
        return userRepository.updateUser(userToBeUpdated);
    }

    /*Ilia*/
    /*ToDo We have mistaken here. isUserTheDriver do not have to throw UnauthorizedOperationException. We are
    *  just checking if the user we are trying to leave feedback for is the driver of the travel we've been on.*/

    @Override
    public Feedback leaveFeedbackForDriver(Feedback feedbackForDriver,
                                           Travel travel,
                                           User loggedUser,
                                           User driver) {
        ValidationHelper.isTravelCompleted(travel, TRAVEL_NOT_COMPLETED_CANNOT_LEAVE_FEEDBACK);
        ValidationHelper.checkNotToBeDriver(travel, loggedUser, INVALID_OPERATION_DRIVER);
        ValidationHelper.isTheUserInTheApprovedListOfTheTravel(loggedUser, travel, USER_NOT_IN_APPROVED_LIST);
        ValidationHelper.isUserTheDriver(travel, driver, UNAUTHORIZED_OPERATION_NOT_DRIVER);
        feedbackForDriver.setReceiver(driver);
        feedbackForDriver.setAuthor(loggedUser);
        feedbackForDriver.setTravel(travel);
        feedbackForDriver = feedbackService.createFeedback(feedbackForDriver);
        Set<Feedback> feedbackForDriverSet = driver.getFeedback();

        /*TODO extract them in helper class*/
        if(!feedbackForDriverSet.stream()
                .filter(feedback -> feedback.getAuthor().equals(loggedUser))
                .filter(feedback -> feedback.getTravel().equals(travel))
                .filter(feedback -> feedback.getReceiver().equals(driver))
                .collect(Collectors.toList()).isEmpty()){
            throw new InvalidOperationException(YOU_HAVE_ALREADY_LEFT_FEEDBACK_FOR_THIS_RIDE);
        }

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
        ValidationHelper.isTravelCompleted(travel, TRAVEL_NOT_COMPLETED_CANNOT_LEAVE_FEEDBACK);
        ValidationHelper.isUserTheDriver(travel, loggedUser, UNAUTHORIZED_OPERATION_NOT_DRIVER);
        ValidationHelper.isTheUserInTheApprovedListOfTheTravel(passenger, travel, USER_NOT_IN_APPROVED_LIST);

        feedbackForPassenger.setReceiver(passenger);
        feedbackForPassenger.setAuthor(loggedUser);
        feedbackForPassenger.setTravel(travel);
        feedbackForPassenger = feedbackService.createFeedback(feedbackForPassenger);
        Set<Feedback> feedbackForPassengerSet = passenger.getFeedback();

        /*TODO extract them in helper class*/
        if(!feedbackForPassengerSet.stream()
                .filter(feedback -> feedback.getAuthor().equals(loggedUser))
                .filter(feedback -> feedback.getTravel().equals(travel))
                .filter(feedback -> feedback.getReceiver().equals(passenger))
                .collect(Collectors.toList()).isEmpty()){
            throw new InvalidOperationException(YOU_HAVE_ALREADY_LEFT_FEEDBACK_FOR_THIS_RIDE);
        }


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
                .filter(feedback -> feedback.getFeedbackType().equals(FeedbackType.FOR_DRIVER))
                .sorted(Comparator.comparing(Feedback::getCreated).reversed())
                .collect(Collectors.toList());
    }

    /*Ilia*/
    @Override
    public List<Feedback> getAllFeedbackForPassenger(User user) {
        return user.getFeedback()
                .stream()
                .filter(feedback -> feedback.getFeedbackType().equals(FeedbackType.FOR_PASSENGER))
                .sorted(Comparator.comparing(Feedback::getCreated).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public User verifyEmail(User userToBeVerified) {
        userToBeVerified.setVerified(true);
        return userRepository.updateUser(userToBeVerified);
    }

    /*Ilia*/
    @Override
    public void deleteUser(User userToBeDeleted, User loggedUser) {
        ValidationHelper.isSameUser(userToBeDeleted, loggedUser, UNAUTHORIZED_OPERATION);
        userToBeDeleted.setDeleted(true);
        userRepository.updateUser(userToBeDeleted);
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
