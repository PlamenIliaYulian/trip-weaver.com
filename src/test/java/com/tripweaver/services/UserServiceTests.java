package com.tripweaver.services;

import com.tripweaver.exceptions.EntityNotFoundException;
import com.tripweaver.exceptions.InvalidOperationException;
import com.tripweaver.exceptions.UnauthorizedOperationException;
import com.tripweaver.helpers.TestHelpers;
import com.tripweaver.models.Feedback;
import com.tripweaver.models.Role;
import com.tripweaver.models.Travel;
import com.tripweaver.models.User;
import com.tripweaver.models.filterOptions.UserFilterOptions;
import com.tripweaver.exceptions.DuplicateEntityException;
import com.tripweaver.exceptions.EntityNotFoundException;
import com.tripweaver.exceptions.InvalidOperationException;
import com.tripweaver.exceptions.UnauthorizedOperationException;
import com.tripweaver.helpers.TestHelpers;
import com.tripweaver.models.*;
import com.tripweaver.repositories.contracts.UserRepository;
import com.tripweaver.services.contracts.FeedbackService;
import com.tripweaver.services.contracts.UserService;
import com.tripweaver.services.contracts.AvatarService;
import com.tripweaver.services.contracts.FeedbackService;
import com.tripweaver.services.contracts.RoleService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import java.util.HashSet;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock
    UserRepository userRepository;
    @Mock
    FeedbackService feedbackService;

    @Mock
    AvatarService avatarService;

    @Mock
    RoleService roleService;

    @InjectMocks
    UserServiceImpl userService;


    @Test
    public void createUser_Should_Throw_When_UsernameIsNotUnique() {
        User createdUser = TestHelpers.createMockUserPlamen();
        User userToBeCreated = TestHelpers.createMockUserPlamen();

        Assertions.assertThrows(DuplicateEntityException.class,
                ()-> userService.createUser(userToBeCreated));
    }


    /*Ilia*/
    @Test
    public void getAllUsers_Should_Throw_When_LoggedUserIsNotAdmin() {
        User mockLoggedUser = TestHelpers.createMockNonAdminUser1$Ilia();
        UserFilterOptions mockUserFilterOptions = TestHelpers.createMockUserFilterOptions$Ilia();

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> userService.getAllUsers(mockUserFilterOptions, mockLoggedUser));

    }

    /*Ilia*/
    @Test
    public void getAllUsers_Should_CallRepository_When_ValidArgumentsPassed() {
        User mockLoggedUser = TestHelpers.createMockNonAdminUser1$Ilia();
        Role mockRoleAdmin = TestHelpers.createMockRoleAdmin$Ilia();
        Set<Role> roleSet = mockLoggedUser.getRoles();
        roleSet.add(mockRoleAdmin);
        UserFilterOptions mockUserFilterOptions = TestHelpers.createMockUserFilterOptions$Ilia();

        userService.getAllUsers(mockUserFilterOptions, mockLoggedUser);

        Mockito.verify(userRepository, Mockito.times(1))
                .getAllUsers(mockUserFilterOptions);
    }

    /*Ilia*/
    @Test
    public void getUserById_Should_CallRepository() {
        userService.getUserById(Mockito.anyInt());

        Mockito.verify(userRepository, Mockito.times(1))
                .getUserById(Mockito.anyInt());
    }

    /*Ilia*/
    @Test
    public void getTopTenTravelPassengersByRating_Should_CallRepository() {
        userService.getTopTenTravelPassengersByRating();

        Mockito.verify(userRepository, Mockito.times(1))
                .getTopTenTravelPassengersByRating();
    }

    /*Ilia*/
    @Test
    public void leaveFeedbackForDriver_Should_Throw_WhenTravelNotCompleted() {
        Feedback mockFeedbackForDriver = TestHelpers.createMockFeedbackForUser1ForDriver$Ilia();
        Travel mockTravel = TestHelpers.createMockTravel1$Ilia();
        User mockLoggedUser = TestHelpers.createMockNonAdminUser2$Ilia();
        User mockDriver = mockTravel.getDriver();

        Assertions.assertThrows(InvalidOperationException.class,
                () -> userService.leaveFeedbackForDriver(mockFeedbackForDriver,
                        mockTravel, mockLoggedUser, mockDriver));
    }

    /*Ilia*/
    @Test
    public void leaveFeedbackForDriver_Should_Throw_When_LoggedUserIsTheDriver() {
        Feedback mockFeedbackForDriver = TestHelpers.createMockFeedbackForUser1ForDriver$Ilia();
        Travel mockTravel = TestHelpers.createMockTravel1$Ilia();
        mockTravel.setStatus(TestHelpers.createMockTravelStatusCompleted$Ilia());
        User mockTravelDriver = mockTravel.getDriver();

        Assertions.assertThrows(InvalidOperationException.class,
                () -> userService.leaveFeedbackForDriver(mockFeedbackForDriver,
                        mockTravel, mockTravelDriver, mockTravelDriver));
    }

    /*Ilia*/
    @Test
    public void leaveFeedbackForDriver_Should_Throw_When_UserIsNotInTheApprovedList() {
        Feedback mockFeedbackForDriver = TestHelpers.createMockFeedbackForUser1ForDriver$Ilia();
        Travel mockTravel = TestHelpers.createMockTravel1$Ilia();
        mockTravel.setStatus(TestHelpers.createMockTravelStatusCompleted$Ilia());
        User mockLoggedUser = TestHelpers.createMockNonAdminUser2$Ilia();
        mockLoggedUser.setUserId(10);
        User mockDriver = mockTravel.getDriver();

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> userService.leaveFeedbackForDriver(mockFeedbackForDriver,
                        mockTravel, mockLoggedUser, mockDriver));
    }

    /*Ilia*/
    @Test
    public void leaveFeedbackForDriver_Should_Throw_When_UserIsTheDriver() {
        Feedback mockFeedbackForDriver = TestHelpers.createMockFeedbackForUser1ForDriver$Ilia();
        Travel mockTravel = TestHelpers.createMockTravel1$Ilia();
        mockTravel.setStatus(TestHelpers.createMockTravelStatusCompleted$Ilia());
        User mockLoggedUser = TestHelpers.createMockNonAdminUser2$Ilia();
        User mockDriver = TestHelpers.createMockNonAdminUser2$Ilia();
        mockDriver.setUserId(10);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> userService.leaveFeedbackForDriver(mockFeedbackForDriver,
                        mockTravel, mockLoggedUser, mockDriver));
    }

    /*Ilia*/
    @Test
    public void leaveFeedbackForDriver_Should_Throw_When_FeedbackAlreadyLeft() {
        Feedback mockFeedbackForDriver = TestHelpers.createMockFeedbackForUser1ForDriver$Ilia();
        Travel mockTravel = TestHelpers.createMockTravel1$Ilia();
        mockTravel.setStatus(TestHelpers.createMockTravelStatusCompleted$Ilia());
        User mockLoggedUser = TestHelpers.createMockNonAdminUser2$Ilia();
        User mockDriver = mockTravel.getDriver();
        Set<Feedback> feedbackSet = mockDriver.getFeedback();
        feedbackSet.add(mockFeedbackForDriver);

        Mockito.when(feedbackService.createFeedback(Mockito.any()))
                .thenReturn(mockFeedbackForDriver);

        Assertions.assertThrows(InvalidOperationException.class,
                () -> userService.leaveFeedbackForDriver(mockFeedbackForDriver,
                        mockTravel, mockLoggedUser, mockDriver));
    }

    /*Ilia*/
    @Test
    public void leaveFeedbackForDriver_Should_CallRepository_When_ValidArgumentsPassed() {
        Feedback mockFeedbackForDriver = TestHelpers.createMockFeedbackForUser1ForDriver$Ilia();
        Travel mockTravel = TestHelpers.createMockTravel1$Ilia();
        mockTravel.setStatus(TestHelpers.createMockTravelStatusCompleted$Ilia());
        User mockLoggedUser = TestHelpers.createMockNonAdminUser2$Ilia();
        User mockDriver = mockTravel.getDriver();

        Mockito.when(feedbackService.createFeedback(Mockito.any()))
                .thenReturn(mockFeedbackForDriver);

        userService.leaveFeedbackForDriver(mockFeedbackForDriver, mockTravel, mockLoggedUser, mockDriver);

        Mockito.verify(userRepository, Mockito.times(1))
                .updateUser(mockDriver);
    }

    /*Ilia*/
    @Test
    public void getAllFeedbackForPassenger_Should_ReturnListOfFeedback_When_Called() {
        User mockLoggedUser = TestHelpers.createMockNonAdminUser1$Ilia();
        Feedback mockFeedbackForPassenger = TestHelpers.createMockFeedbackForUser1ForPassenger$Ilia();
        Set<Feedback> feedbackSet = mockLoggedUser.getFeedback();
        feedbackSet.add(mockFeedbackForPassenger);
        List<Feedback> feedbackList = userService.getAllFeedbackForPassenger(mockLoggedUser);

        Assertions.assertEquals(feedbackSet.size(), feedbackList.size());
    }

    /*Ilia*/
    @Test
    public void deleteUser_Should_Throw_When_UserIsNotTheSameAsLoggedUser() {
        User mockUserToBeDeleted = TestHelpers.createMockNonAdminUser1$Ilia();
        User mockUserLogged = TestHelpers.createMockNonAdminUser2$Ilia();

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> userService.deleteUser(mockUserToBeDeleted, mockUserLogged));
    }

    /*Ilia*/
    @Test
    public void deleteUser_Should_CallRepository_When_ValidArgumentsPassed() {
        User mockUserToBeDeleted = TestHelpers.createMockNonAdminUser1$Ilia();

        userService.deleteUser(mockUserToBeDeleted, mockUserToBeDeleted);

        Mockito.verify(userRepository, Mockito.times(1))
                .updateUser(mockUserToBeDeleted);

                () -> userService.createUser(userToBeCreated));
    }

    @Test
    public void createUser_Should_Throw_When_EmailIsNotUnique() {
        User createdUser = TestHelpers.createMockUserPlamen();
        User userToBeCreated = TestHelpers.createMockUserPlamen();

        Assertions.assertThrows(DuplicateEntityException.class,
                () -> userService.createUser(userToBeCreated));
    }

    @Test
    public void createUser_Should_Throw_When_PhoneNumberIsNotUnique() {
        User createdUser = TestHelpers.createMockUserPlamen();
        User userToBeCreated = TestHelpers.createMockUserPlamen();

        Assertions.assertThrows(DuplicateEntityException.class,
                () -> userService.createUser(userToBeCreated));
    }

    @Test
    public void createUser_Should_CallRepository() {
        User userToBeCreated = TestHelpers.createMockUserPlamen();
        Role role = TestHelpers.createMockRolePlamen();


        Mockito.when(userRepository.getUserByUsername(userToBeCreated.getUsername()))
                .thenThrow(new EntityNotFoundException("User", "username", userToBeCreated.getUsername()));

        Mockito.when(userRepository.getUserByEmail(Mockito.anyString()))
                .thenThrow(new EntityNotFoundException("User", "email", userToBeCreated.getEmail()));

        Mockito.when(userRepository.getUserByPhoneNumber(Mockito.anyString()))
                .thenThrow(new EntityNotFoundException("User", "email", userToBeCreated.getPhoneNumber()));

        Mockito.when(avatarService.getDefaultAvatar())
                .thenReturn(new Avatar());

        Mockito.when(roleService.getRoleById(Mockito.anyInt()))
                .thenReturn(new Role());

        userService.createUser(userToBeCreated);


        Mockito.verify(userRepository, Mockito.times(1))
                .createUser(Mockito.any(User.class));
    }

    @Test
    public void getUserByUsername_Should_CallRepository(){
        userService.getUserByUsername("username");

        Mockito.verify(userRepository, Mockito.times(1))
                .getUserByUsername("username");
    }

    @Test
    public void blockUser_Should_Throw_When_UserIsNotAdmin(){
        User userNotAdmin = TestHelpers.createMockUserPlamen();
        User userToBeBlocked = TestHelpers.createMockUserPlamen();

        Assertions.assertThrows(UnauthorizedOperationException.class,
                ()-> userService.blockUser(userToBeBlocked, userNotAdmin));

    }

    @Test
    public void blockUser_Should_CallRepository_When_ArgumentsAreValid(){
        User userAdmin = TestHelpers.createMockUserPlamen();
        User userToBeBlocked = TestHelpers.createMockUserPlamen();
        Role adminRole = TestHelpers.createMockRolePlamen();
        adminRole.setRoleId(1);
        userAdmin.getRoles().add(adminRole);

        userService.blockUser(userToBeBlocked, userAdmin);

        Mockito.verify(userRepository, Mockito.times(1))
                .updateUser(userToBeBlocked);

        Assertions.assertTrue(userToBeBlocked.isBlocked());
    }

    @Test
    public void getAllUsersCount_Should_CallRepository(){
        userService.getAllUsersCount();

        Mockito.verify(userRepository, Mockito.times(1))
                .getAllUsersCount();
    }

    @Test
    public void addAvatar_Should_Throw_When_UserIsSameUser(){
        User userToBeUpdated = TestHelpers.createMockUserPlamen();
        User userToDoChanges = TestHelpers.createMockUserPlamen();
        userToDoChanges.setUserId(2);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                ()-> userService.addAvatar(userToBeUpdated,"avatarUrl", userToDoChanges));
    }

    @Test
    public void addAvatar_Should_CallRepository(){
        User userToBeUpdated = TestHelpers.createMockUserPlamen();
        User userToDoChanges = TestHelpers.createMockUserPlamen();

        userService.addAvatar(userToBeUpdated,"avatarUrl",userToDoChanges);

        Mockito.verify(userRepository, Mockito.times(1))
                .updateUser(userToBeUpdated);
    }

    @Test
    public void leaveFeedbackForPassenger_Should_Throw_When_TravelIsNotCompleted(){
        Travel travel = TestHelpers.createMockTravelPlamen();
        User loggedUser = TestHelpers.createMockUserPlamen();
        User passenger = TestHelpers.createMockUserPlamen();
        Feedback feedback = TestHelpers.createFeedbackPlamen();

        Assertions.assertThrows(InvalidOperationException.class,
                ()-> userService.leaveFeedbackForPassenger(feedback, travel, loggedUser, passenger));
    }

    @Test
    public void leaveFeedbackForPassenger_Should_Throw_When_UserIsNotDriver(){
        Travel travel = TestHelpers.createMockTravelPlamen();
        User loggedUser = TestHelpers.createMockUserPlamen();
        User passenger = TestHelpers.createMockUserPlamen();
        Feedback feedback = TestHelpers.createFeedbackPlamen();
        loggedUser.setUserId(2);
        TravelStatus completedStatus = TestHelpers.createMockTravelStatusPlamen();
        completedStatus.setTravelStatusId(3);
        travel.setStatus(completedStatus);
        travel.getUsersApprovedForTheTravel().add(passenger);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                ()-> userService.leaveFeedbackForPassenger(feedback, travel, loggedUser, passenger));
    }

    @Test
    public void leaveFeedbackForPassenger_Should_Throw_When_PassengerNotInTheApprovedList(){
        Travel travel = TestHelpers.createMockTravelPlamen();
        User loggedUser = TestHelpers.createMockUserPlamen();
        User passenger = TestHelpers.createMockUserPlamen();
        Feedback feedback = TestHelpers.createFeedbackPlamen();
        TravelStatus completedStatus = TestHelpers.createMockTravelStatusPlamen();
        completedStatus.setTravelStatusId(3);
        travel.setStatus(completedStatus);

        Assertions.assertThrows(EntityNotFoundException.class,
                ()-> userService.leaveFeedbackForPassenger(feedback, travel, loggedUser, passenger));
    }

    @Test
    public void leaveFeedbackForPassenger_Should_CallRepository(){
        Travel travel = TestHelpers.createMockTravelPlamen();
        User loggedUser = TestHelpers.createMockUserPlamen();
        User passenger = TestHelpers.createMockUserPlamen();
        Feedback feedback = TestHelpers.createFeedbackPlamen();
        Feedback feedback2 = TestHelpers.createFeedbackPlamen();
        feedback2.setFeedbackId(2);
        Set<Feedback> feedbackSet = new HashSet<>();
        feedbackSet.add(feedback2);
        passenger.setFeedback(feedbackSet);
        TravelStatus completedStatus = TestHelpers.createMockTravelStatusPlamen();
        completedStatus.setTravelStatusId(3);
        travel.setStatus(completedStatus);
        travel.getUsersApprovedForTheTravel().add(passenger);

        Mockito.when(feedbackService.createFeedback(feedback))
                        .thenReturn(feedback);

        userService.leaveFeedbackForPassenger(feedback, travel, loggedUser, passenger);

        Mockito.verify(userRepository, Mockito.times(1))
                .updateUser(passenger);
    }
}
