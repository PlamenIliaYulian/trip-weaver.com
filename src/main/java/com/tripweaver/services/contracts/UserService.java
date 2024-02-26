package com.tripweaver.services.contracts;

import com.tripweaver.models.*;
import com.tripweaver.models.filterOptions.UserFilterOptions;

import java.util.List;

public interface UserService {


    /*ToDo Plamen*/
    User createUser(User user);
    /*ToDo Yuli - DONE*/
    User updateUser(User user, User loggedUser);
    /*ToDo Ilia*/
    List<User> getAllUsers(UserFilterOptions userFilterOptions, User loggedInUser);
    /*ToDo Plamen*/
    User getUserByUsername(String username);
    /*ToDo Yuli - DONE*/
    User getUserByEmail(String email);
    /*ToDo Ilia*/
    User getUserById(int id);
    /*ToDo Plamen*/
    User blockUser(User userToBeBlocked, User loggedUser);
    /*ToDo Yuli - DONE*/
    User unBlockUser(User userToBeUnBlocked, User loggedUser);
    /*ToDo Ilia*/
    User getUserByPhoneNumber(String phoneNumber);
    /*ToDo Plamen*/
    long getAllUsersCount();
    /*ToDo Yuli - DONE*/
    List<User> getTopTenTravelOrganizersByRating();
    /*ToDo Ilia*/
    List<User> getTopTenTravelPassengersByRating();
    /*ToDo Plamen*/
    User addAvatar(User userToBeUpdated, String avatarUrl, User loggedUser);
    /*ToDo Yuli - DONE*/
    User deleteAvatar(User userToBeUpdated, User loggedUser);
    /*ToDo Ilia*/
    User leaveFeedbackForDriver(FeedbackForDriver feedbackForDriver,
                                Travel travel,
                                User userToReceiveFeedback,
                                User userToGiveFeedback);
    /*ToDo Plamen*/
    User leaveFeedbackForPassenger(FeedbackForPassenger feedbackForPassenger, User userToReceiveFeedback);
    /*ToDo Yuli*/
    List<FeedbackForDriver> getAllFeedbackForDriver(User user);
    /*ToDo Ilia*/
    List<FeedbackForPassenger> getAllFeedbackForPassenger(User user);
}
