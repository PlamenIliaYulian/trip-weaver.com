package com.tripweaver.services.contracts;

import com.tripweaver.models.User;
import com.tripweaver.models.UserFilterOptions;

import java.util.List;

public interface UserService {
    User createUser(User user);

    User updateUser(User user);

    List<User> getAllUsers(UserFilterOptions userFilterOptions);

    User getUserByUsername(String username);

    User getUserByEmail(String email);

    User getUserById(int id);

    User blockUser(User userToBeBlocked);

    User unBlockUser(User userToBeUnBlocked);

    User getUserByPhoneNumber(String phoneNumber);
}
