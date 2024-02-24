package com.tripweaver.services.contracts;

import com.tripweaver.models.FeedbackForDriver;
import com.tripweaver.models.FeedbackForPassenger;
import com.tripweaver.models.User;
import com.tripweaver.models.UserFilterOptions;

import java.util.List;

public interface UserService {


    /*ToDo Plamen*/
    User createUser(User user);

    /*ToDo Yuli*/
    User updateUser(User user);

    /*ToDo Ilia*/
    List<User> getAllUsers(UserFilterOptions userFilterOptions);
    /*ToDo Plamen*/
    User getUserByUsername(String username);
    /*ToDo Yuli*/
    User getUserByEmail(String email);
    /*ToDo Ilia*/
    User getUserById(int id);
    /*ToDo Plamen*/
    User blockUser(User userToBeBlocked);

    /*ToDo Yuli*/
    User unBlockUser(User userToBeUnBlocked);

    /*ToDo Ilia*/
    User getUserByPhoneNumber(String phoneNumber);

    /*ToDo Plamen*/
    long getAllUsersCount();

    /*ToDo Yuli*/
    List<User> getTopTenTravelOrganizersByRating();

    /*ToDo Ilia*/
    List<User> getTopTenTravelPassengersByRating();
    /*ToDo Plamen*/
    User addAvatar(User userToBeUpdated, String avatar, User loggedUser);
    /*ToDo Yuli*/
    User deleteAvatar(User userToBeUpdated, User loggedUser);
    /*ToDo Ilia*/
    User leaveFeedbackForDriver(FeedbackForDriver feedbackForDriver, User userToReceiveFeedback);
    /*ToDo Plamen*/
    User leaveFeedbackForPassenger(FeedbackForPassenger feedbackForPassenger, User userToReceiveFeedback);
    /*ToDo Yuli*/
    List<FeedbackForDriver> getAllFeedbackForDriver(User user);
    /*ToDo Ilia*/
    List<FeedbackForPassenger> getAllFeedbackForPassenger(User user);
}
