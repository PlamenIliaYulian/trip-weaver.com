package com.tripweaver.repositories.contracts;

import com.tripweaver.models.FeedbackForDriver;
import com.tripweaver.models.FeedbackForPassenger;
import com.tripweaver.models.User;
import com.tripweaver.models.UserFilterOptions;

import java.util.List;

public interface UserRepository {

    User createUser(User user);
    User updateUser(User user);
    List<User> getAllUsers(UserFilterOptions userFilterOptions);
    User getUserByUsername(String username);
    User getUserByEmail(String email);
    User getUserById(int id);
    User blockUser(User userToBeBlocked);
    User unBlockUser(User userToBeUnBlocked);
    User getUserByPhoneNumber(String phoneNumber);
    long getAllUsersCount();
    List<User> getTopTenTravelOrganizersByRating();
    List<User> getTopTenTravelPassengersByRating();
    User addAvatar(User userToBeUpdated, String avatar, User loggedUser);
    User deleteAvatar(User userToBeUpdated, User loggedUser);
    User leaveFeedbackForDriver(FeedbackForDriver feedbackForDriver, User userToReceiveFeedback);
    User leaveFeedbackForPassenger(FeedbackForPassenger feedbackForPassenger, User userToReceiveFeedback);
    List<FeedbackForDriver> getAllFeedbackForDriver(User user);
    List<FeedbackForPassenger> getAllFeedbackForPassenger(User user);


}
