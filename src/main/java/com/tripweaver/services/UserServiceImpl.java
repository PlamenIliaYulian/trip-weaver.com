package com.tripweaver.services;

import com.tripweaver.exceptions.DuplicateEntityException;
import com.tripweaver.exceptions.EntityNotFoundException;
import com.tripweaver.exceptions.UnauthorizedOperationException;
import com.tripweaver.models.*;
import com.tripweaver.repositories.contracts.UserRepository;
import com.tripweaver.services.contracts.AvatarService;
import com.tripweaver.services.contracts.AvatarService;
import com.tripweaver.services.contracts.RoleService;
import com.tripweaver.services.contracts.UserService;
import com.tripweaver.services.helpers.PermissionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    public static final String UNAUTHORIZED_OPERATION = "Unauthorized operation.";
    private final UserRepository userRepository;
    private final PermissionHelper permissionHelper;

    private final AvatarService avatarService;
    private final RoleService roleService;
    private final AvatarService avatarService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PermissionHelper permissionHelper, AvatarService avatarService) {
        this.userRepository = userRepository;
        this.permissionHelper = permissionHelper;
        this.avatarService = avatarService;
        this.roleService = roleService;
        this.avatarService = avatarService;
    }

    @Override
    public User createUser(User user) {
        checkForUniqueUsername(user);
        checkForUniqueEmail(user);
        checkIfPhoneNumberUnique(user);
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
    public User blockUser(User userToBeBlocked, User loggedUser) {
        if(!loggedUser.getRoles().contains(roleService.getRoleById(1))){
            throw new UnauthorizedOperationException(UNAUTHORIZED_OPERATION);
        }
        userToBeBlocked.setBlocked(true);
        return userRepository.updateUser(userToBeBlocked);
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
        return userRepository.getAllUsersCount();
    }

    @Override
    public List<User> getTopTenTravelOrganizersByRating() {
        return userRepository.getTopTenTravelOrganizersByRating();
    }

    @Override
    public List<User> getTopTenTravelPassengersByRating() {
        return null;
    }

    @Override
    public User addAvatar(User userToBeUpdated, String avatar, User loggedUser) {
        if(!userToBeUpdated.equals(loggedUser)){
            throw new UnauthorizedOperationException(UNAUTHORIZED_OPERATION);
        }
        Avatar avatarToAdd = new Avatar();
        avatarToAdd.setAvatarUrl(avatar);
        avatarToAdd = avatarService.createAvatar(avatarToAdd);

        userToBeUpdated.setAvatar(avatarToAdd);
        return userRepository.updateUser(userToBeUpdated);
    }

    @Override
    public User deleteAvatar(User userToBeUpdated, User loggedUser) {
        PermissionHelper.isAdminOrSameUser(
                userRepository.getUserById(userToBeUpdated.getUserId()),
                loggedUser, UNAUTHORIZED_OPERATION);
        userToBeUpdated.setAvatar(avatarService.getDefaultAvatar());
        return userRepository.updateUser(userToBeUpdated);
    }

    @Override
    public User leaveFeedbackForDriver(FeedbackForDriver feedbackForDriver, User userToReceiveFeedback) {
        return null;
    }

    /*TODO not sure if this is the correct implementation*/
    @Override
    public User leaveFeedbackForPassenger(FeedbackForPassenger feedbackForPassenger, User userToReceiveFeedback) {
        userToReceiveFeedback.getFeedbackForPassenger().add(feedbackForPassenger);
        return userRepository.updateUser(userToReceiveFeedback);
    }

    @Override
    public List<FeedbackForDriver> getAllFeedbackForDriver(User user) {
        return null;
    }

    @Override
    public List<FeedbackForPassenger> getAllFeedbackForPassenger(User user) {
        return null;
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
