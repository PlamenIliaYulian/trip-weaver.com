package com.tripweaver.services;

import com.tripweaver.models.FeedbackForDriver;
import com.tripweaver.models.FeedbackForPassenger;
import com.tripweaver.models.User;
import com.tripweaver.models.UserFilterOptions;
import com.tripweaver.repositories.contracts.UserRepository;
import com.tripweaver.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(User user) {
        return userRepository.createUser(user);
    }

    @Override
    public User updateUser(User user) {
        return userRepository.updateUser(user);
    }

    @Override
    public List<User> getAllUsers(UserFilterOptions userFilterOptions) {
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
    public User blockUser(User userToBeBlocked) {
        return userRepository.blockUser(userToBeBlocked);
    }

    @Override
    public User unBlockUser(User userToBeUnBlocked) {
        userToBeUnBlocked.setBlocked(false);
        return userRepository.updateUser(userToBeUnBlocked);
    }

    @Override
    public User getUserByPhoneNumber(String phoneNumber) {
        return userRepository.getUserByPhoneNumber(phoneNumber);
    }

    @Override
    public long getAllUsersCount() {
        return 0;
    }

    @Override
    public List<User> getTopTenTravelOrganizersByRating() {
        return null;
    }

    @Override
    public List<User> getTopTenTravelPassengersByRating() {
        return null;
    }

    @Override
    public User addAvatar(User userToBeUpdated, String avatar, User loggedUser) {
        return null;
    }

    @Override
    public User deleteAvatar(User userToBeUpdated, User loggedUser) {
        return null;
    }

    @Override
    public User leaveFeedbackForDriver(FeedbackForDriver feedbackForDriver, User userToReceiveFeedback) {
        return null;
    }

    @Override
    public User leaveFeedbackForPassenger(FeedbackForPassenger feedbackForPassenger, User userToReceiveFeedback) {
        return null;
    }

    @Override
    public List<FeedbackForDriver> getAllFeedbackForDriver(User user) {
        return null;
    }

    @Override
    public List<FeedbackForPassenger> getAllFeedbackForPassenger(User user) {
        return null;
    }
}
