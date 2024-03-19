package com.tripweaver.services.contracts;

import com.tripweaver.models.Feedback;
import com.tripweaver.models.Travel;
import com.tripweaver.models.User;
import com.tripweaver.models.enums.EmailVerificationType;
import com.tripweaver.models.filterOptions.UserFilterOptions;

import java.util.HashMap;
import java.util.List;

public interface UserService {

    User createUser(User user);

    User updateUser(User user, User loggedUser);

    List<User> getAllUsers(UserFilterOptions userFilterOptions, User loggedInUser);

    User getUserByUsername(String username);

    User getUserByEmail(String email);

    User getUserById(int id);

    User blockUser(User userToBeBlocked, User loggedUser);

    User unBlockUser(User userToBeUnBlocked, User loggedUser);

    long getAllUsersCount();

    List<User> getTopTwelveTravelOrganizersByRating();

    List<User> getTopTwelveTravelPassengersByRating();

    User addAvatar(User userToBeUpdated, String avatarUrl, User loggedUser);

    User deleteAvatar(User userToBeUpdated, User loggedUser);

    Feedback leaveFeedbackForDriver(Feedback feedbackForDriver, Travel travel, User userToGiveFeedback, User driver);

    Feedback leaveFeedbackForPassenger(Feedback feedbackForPassenger, Travel travel, User loggedUser, User passenger);

    List<Feedback> getAllFeedbackForDriver(User user);

    List<Feedback> getAllFeedbackForPassenger(User user);

    User verifyEmail(User userToBeVerified, EmailVerificationType emailVerificationType);

    void deleteUser(User userToBeDeleted, User loggedUser);

    HashMap<String, Integer> getTotalTravelsAsPassengerHashMap(List<User> passengers);

    HashMap<String, Integer> getTotalDistanceAsPassengerHashMap(List<User> passengers);

    HashMap<String, Integer> getTotalTravelsAsDriverHashMap(List<User> drivers);

    HashMap<String, Integer> getTotalDistanceAsDriverHashMap(List<User> drivers);

    User addCarPicture(User userToBeUpdated, String carPictureUrl, User loggedUser);

    User deleteCarPicture(User userToBeUpdated, User loggedUser);

}
