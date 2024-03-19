package com.tripweaver.services;

import com.tripweaver.exceptions.DuplicateEntityException;
import com.tripweaver.exceptions.EntityNotFoundException;
import com.tripweaver.models.*;
import com.tripweaver.models.enums.EmailVerificationType;
import com.tripweaver.models.enums.FeedbackType;
import com.tripweaver.models.filterOptions.TravelFilterOptions;
import com.tripweaver.models.filterOptions.UserFilterOptions;
import com.tripweaver.repositories.contracts.UserRepository;
import com.tripweaver.services.contracts.*;
import com.tripweaver.services.helpers.ValidationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.tripweaver.services.helpers.ConstantHelper.*;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final AvatarService avatarService;
    private final RoleService roleService;
    private final FeedbackService feedbackService;
    private final TravelService travelService;

    private final CarPictureService carPictureService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           AvatarService avatarService,
                           RoleService roleService,
                           FeedbackService feedbackService,
                           TravelService travelService,
                           CarPictureService carPictureService) {
        this.userRepository = userRepository;
        this.avatarService = avatarService;
        this.roleService = roleService;
        this.feedbackService = feedbackService;
        this.travelService = travelService;
        this.carPictureService = carPictureService;
    }

    @Override
    public User createUser(User user) {
        checkForUniqueUsername(user);
        checkForUniqueEmail(user);
        checkIfPhoneNumberUnique(user);
        user.setCreated(LocalDateTime.now());
        user.setAvatar(avatarService.getDefaultAvatar());
        user.setCarPicture(carPictureService.getDefaultCarPicture());
        HashSet<Role> roles = new HashSet<>();
        roles.add(roleService.getRoleById(2));
        user.setRoles(roles);
        return userRepository.createUser(user);
    }

    @Override
    public User updateUser(User user, User loggedUser) {
        ValidationHelper.isSameUser(user, loggedUser, UNAUTHORIZED_OPERATION);
        checkForUniqueEmail(user);
        checkIfPhoneNumberUnique(user);
        return userRepository.updateUser(user);
    }

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

    @Override
    public long getAllUsersCount() {
        return userRepository.getAllUsersCount();
    }

    @Override
    public List<User> getTopTwelveTravelOrganizersByRating() {
        return userRepository.getTopTwelveTravelOrganizersByRating();
    }

    @Override
    public List<User> getTopTwelveTravelPassengersByRating() {
        return userRepository.getTopTwelveTravelPassengersByRating();
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

    @Override
    public Feedback leaveFeedbackForDriver(Feedback feedbackForDriver,
                                           Travel travel,
                                           User loggedUser,
                                           User driver) {
        ValidationHelper.isTravelCompleted(travel, TRAVEL_NOT_COMPLETED_CANNOT_LEAVE_FEEDBACK);
        ValidationHelper.checkNotToBeDriver(travel, loggedUser, INVALID_OPERATION_DRIVER);
        ValidationHelper.isTheUserInTheApprovedListOfTheTravel(loggedUser, travel, USER_NOT_IN_APPROVED_LIST);
        ValidationHelper.isUserTheDriver(travel, driver, INVALID_OPERATION_NOT_DRIVER);
        
        Set<Feedback> feedbackForDriverSet = driver.getFeedback();
        ValidationHelper.hasUserAlreadyLeftFeedbackForThisTravel(travel, loggedUser, driver, feedbackForDriverSet);

        feedbackForDriver.setReceiver(driver);
        feedbackForDriver.setAuthor(loggedUser);
        feedbackForDriver.setTravel(travel);
        feedbackForDriver = feedbackService.createFeedback(feedbackForDriver);

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

        Set<Feedback> feedbackForPassengerSet = passenger.getFeedback();
        ValidationHelper.hasUserAlreadyLeftFeedbackForThisTravel(travel, loggedUser, passenger, feedbackForPassengerSet);

        feedbackForPassenger.setReceiver(passenger);
        feedbackForPassenger.setAuthor(loggedUser);
        feedbackForPassenger.setTravel(travel);
        feedbackForPassenger = feedbackService.createFeedback(feedbackForPassenger);

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

    @Override
    public void deleteUser(User userToBeDeleted, User loggedUser) {
        ValidationHelper.isSameUser(userToBeDeleted, loggedUser, UNAUTHORIZED_OPERATION);
        userToBeDeleted.setDeleted(true);
        userRepository.updateUser(userToBeDeleted);
    }

    @Override
    public HashMap<String, Integer> getTotalTravelsAsPassengerHashMap(List<User> passengers) {
        HashMap<String, Integer> totalTravelsAsPassengerHashMap = new HashMap<>();
        for (User passenger : passengers) {
            int totalTravels = travelService
                    .getTravelsByPassenger(passenger, passenger, new TravelFilterOptions())
                    .stream()
                    .filter(travel -> travel.getStatus().getTravelStatusId() == TRAVEL_STATUS_COMPLETED)
                    .toList()
                    .size();
            totalTravelsAsPassengerHashMap.put(passenger.getUsername(), totalTravels);
        }
        return totalTravelsAsPassengerHashMap;
    }

    @Override
    public HashMap<String, Integer> getTotalDistanceAsPassengerHashMap(List<User> passengers) {
        HashMap<String, Integer> totalDistanceAsPassengerHashMap = new HashMap<>();
        for (User passenger : passengers) {
            int totalDistance = travelService.getTravelsByPassenger(passenger, passenger, new TravelFilterOptions())
                    .stream()
                    .filter(travel -> travel.getStatus().getTravelStatusId() == TRAVEL_STATUS_COMPLETED)
                    .map(Travel::getDistanceInKm)
                    .reduce(0, Integer::sum);
            totalDistanceAsPassengerHashMap.put(passenger.getUsername(), totalDistance);
        }
        return totalDistanceAsPassengerHashMap;
    }

    @Override
    public HashMap<String, Integer> getTotalTravelsAsDriverHashMap(List<User> drivers) {
        HashMap<String, Integer> totalTravelsAsDriverHashMap = new HashMap<>();
        for (User driver : drivers) {
            int totalTravels = travelService
                    .getTravelsByDriver(driver, driver, new TravelFilterOptions())
                    .stream()
                    .filter(travel -> travel.getStatus().getTravelStatusId() == TRAVEL_STATUS_COMPLETED)
                    .toList()
                    .size();
            totalTravelsAsDriverHashMap.put(driver.getUsername(), totalTravels);
        }
        return totalTravelsAsDriverHashMap;
    }

    @Override
    public HashMap<String, Integer> getTotalDistanceAsDriverHashMap(List<User> drivers) {
        HashMap<String, Integer> totalDistancAsDrivereHashMap = new HashMap<>();
        for (User driver : drivers) {
            int totalDistance = travelService.getTravelsByDriver(driver, driver, new TravelFilterOptions())
                    .stream()
                    .filter(travel -> travel.getStatus().getTravelStatusId() == TRAVEL_STATUS_COMPLETED)
                    .map(Travel::getDistanceInKm)
                    .reduce(0, Integer::sum);
            totalDistancAsDrivereHashMap.put(driver.getUsername(), totalDistance);
        }
        return totalDistancAsDrivereHashMap;
    }

    @Override
    public User addCarPicture(User userToBeUpdated, String carPictureUrl, User loggedUser) {
        ValidationHelper.isSameUser(userToBeUpdated, loggedUser, UNAUTHORIZED_OPERATION_NOT_SAME_USER);
        CarPicture carPictureToAdd = new CarPicture();
        carPictureToAdd.setCarPictureUrl(carPictureUrl);
        carPictureToAdd = carPictureService.createCarPicture(carPictureToAdd);

        userToBeUpdated.setCarPicture(carPictureToAdd);
        return userRepository.updateUser(userToBeUpdated);
    }

    @Override
    public User deleteCarPicture(User userToBeUpdated, User loggedUser) {
        ValidationHelper.isAdminOrSameUser(userToBeUpdated, loggedUser, UNAUTHORIZED_OPERATION);
        userToBeUpdated.setCarPicture(carPictureService.getDefaultCarPicture());
        return userRepository.updateUser(userToBeUpdated);
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
