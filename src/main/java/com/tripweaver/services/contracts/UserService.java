package com.tripweaver.services.contracts;

import com.tripweaver.models.*;
import com.tripweaver.models.filterOptions.UserFilterOptions;

import java.util.List;

public interface UserService {


    /*Plamen*/
    User createUser(User user);

    /*Yuli - DONE*/
    User updateUser(User user, User loggedUser);

    /*Ilia*/
    List<User> getAllUsers(UserFilterOptions userFilterOptions, User loggedInUser);

    /*Plamen*/
    User getUserByUsername(String username);

    /*Yuli - DONE*/
    User getUserByEmail(String email);

    /*Ilia*/
    User getUserById(int id);

    /*Plamen*/
    User blockUser(User userToBeBlocked, User loggedUser);

    /*Yuli - DONE*/
    User unBlockUser(User userToBeUnBlocked, User loggedUser);

    /*Ilia*/
    User getUserByPhoneNumber(String phoneNumber);

    /*Plamen*/
    long getAllUsersCount();

    /*Yuli - DONE*/
    List<User> getTopTenTravelOrganizersByRating();

    /*Ilia*/
    List<User> getTopTenTravelPassengersByRating();

    /*Plamen*/
    User addAvatar(User userToBeUpdated, String avatarUrl, User loggedUser);

    /*Yuli - DONE*/
    User deleteAvatar(User userToBeUpdated, User loggedUser);

    /*Ilia*/
    Feedback leaveFeedbackForDriver(Feedback feedbackForDriver,Travel travel,User userToGiveFeedback, User driver);

    /*Plamen*/
    Feedback leaveFeedbackForPassenger(Feedback feedbackForPassenger,Travel travel,User loggedUser, User passenger);

    /*Yuli*/
    List<Feedback> getAllFeedbackForDriver(User user);

    /*Ilia*/
    List<Feedback> getAllFeedbackForPassenger(User user);

}
