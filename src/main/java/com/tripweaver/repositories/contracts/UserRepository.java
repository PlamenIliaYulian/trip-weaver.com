package com.tripweaver.repositories.contracts;

import com.tripweaver.models.User;
import com.tripweaver.models.filterOptions.UserFilterOptions;

import java.util.List;

public interface UserRepository {

    User createUser(User user);

    User updateUser(User user);

    List<User> getAllUsers(UserFilterOptions userFilterOptions);

    User getUserByUsername(String username);

    User getUserByEmail(String email);

    User getUserById(int id);

    User getUserByPhoneNumber(String phoneNumber);

    long getAllUsersCount();

    List<User> getTopTwelveTravelOrganizersByRating();

    List<User> getTopTwelveTravelPassengersByRating();
}
