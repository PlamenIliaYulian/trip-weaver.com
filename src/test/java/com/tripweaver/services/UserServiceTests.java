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
import com.tripweaver.models.*;
import com.tripweaver.repositories.contracts.UserRepository;
import com.tripweaver.services.contracts.FeedbackService;
import com.tripweaver.services.contracts.AvatarService;
import com.tripweaver.services.contracts.RoleService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
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
        User createdUser = TestHelpers.createMockNonAdminUser1();
        User userToBeCreated = TestHelpers.createMockNonAdminUser1();

        Assertions.assertThrows(DuplicateEntityException.class,
                () -> userService.createUser(userToBeCreated));
    }


    /*Ilia*/
    @Test
    public void getAllUsers_Should_Throw_When_LoggedUserIsNotAdmin() {
        User mockLoggedUser = TestHelpers.createMockNonAdminUser1();
        UserFilterOptions mockUserFilterOptions = TestHelpers.createMockUserFilterOptions();

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> userService.getAllUsers(mockUserFilterOptions, mockLoggedUser));

    }

    /*Ilia*/
    @Test
    public void getAllUsers_Should_CallRepository_When_ValidArgumentsPassed() {
        User mockLoggedUser = TestHelpers.createMockNonAdminUser1();
        Role mockRoleAdmin = TestHelpers.createMockRoleAdmin();
        Set<Role> roleSet = mockLoggedUser.getRoles();
        roleSet.add(mockRoleAdmin);
        UserFilterOptions mockUserFilterOptions = TestHelpers.createMockUserFilterOptions();

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
        userService.getTopTwelveTravelPassengersByRating();

        Mockito.verify(userRepository, Mockito.times(1))
                .getTopTwelveTravelPassengersByRating();
    }

    /*Ilia*/
    @Test
    public void leaveFeedbackForDriver_Should_Throw_WhenTravelNotCompleted() {
        Feedback mockFeedbackForDriver = TestHelpers.createMockFeedbackForUser1ForDriver();
        Travel mockTravel = TestHelpers.createMockTravel1();
        User mockLoggedUser = TestHelpers.createMockNonAdminUser2();
        User mockDriver = mockTravel.getDriver();

        Assertions.assertThrows(InvalidOperationException.class,
                () -> userService.leaveFeedbackForDriver(mockFeedbackForDriver,
                        mockTravel, mockLoggedUser, mockDriver));
    }

    /*Ilia*/
    @Test
    public void leaveFeedbackForDriver_Should_Throw_When_LoggedUserIsTheDriver() {
        Feedback mockFeedbackForDriver = TestHelpers.createMockFeedbackForUser1ForDriver();
        Travel mockTravel = TestHelpers.createMockTravel1();
        mockTravel.setStatus(TestHelpers.createMockTravelStatusCompleted());
        User mockTravelDriver = mockTravel.getDriver();

        Assertions.assertThrows(InvalidOperationException.class,
                () -> userService.leaveFeedbackForDriver(mockFeedbackForDriver,
                        mockTravel, mockTravelDriver, mockTravelDriver));
    }

    /*Ilia*/
    @Test
    public void leaveFeedbackForDriver_Should_Throw_When_UserIsNotInTheApprovedList() {
        Feedback mockFeedbackForDriver = TestHelpers.createMockFeedbackForUser1ForDriver();
        Travel mockTravel = TestHelpers.createMockTravel1();
        mockTravel.setStatus(TestHelpers.createMockTravelStatusCompleted());
        User mockLoggedUser = TestHelpers.createMockNonAdminUser2();
        mockLoggedUser.setUserId(10);
        User mockDriver = mockTravel.getDriver();

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> userService.leaveFeedbackForDriver(mockFeedbackForDriver,
                        mockTravel, mockLoggedUser, mockDriver));
    }

    /*Ilia*/
    @Test
    public void leaveFeedbackForDriver_Should_Throw_When_UserIsTheDriver() {
        Feedback mockFeedbackForDriver = TestHelpers.createMockFeedbackForUser1ForDriver();
        Travel mockTravel = TestHelpers.createMockTravel1();
        mockTravel.setStatus(TestHelpers.createMockTravelStatusCompleted());
        User mockLoggedUser = TestHelpers.createMockNonAdminUser2();
        User mockDriver = TestHelpers.createMockNonAdminUser2();
        mockDriver.setUserId(10);

        Assertions.assertThrows(InvalidOperationException.class,
                () -> userService.leaveFeedbackForDriver(mockFeedbackForDriver,
                        mockTravel, mockLoggedUser, mockDriver));
    }

    /*Ilia*/
    @Test
    public void leaveFeedbackForDriver_Should_Throw_When_FeedbackAlreadyLeft() {
        Feedback mockFeedbackForDriver = TestHelpers.createMockFeedbackForUser1ForDriver();
        Travel mockTravel = TestHelpers.createMockTravel1();
        mockTravel.setStatus(TestHelpers.createMockTravelStatusCompleted());
        User mockLoggedUser = TestHelpers.createMockNonAdminUser2();
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
        Feedback mockFeedbackForDriver = TestHelpers.createMockFeedbackForUser1ForDriver();
        Travel mockTravel = TestHelpers.createMockTravel1();
        mockTravel.setStatus(TestHelpers.createMockTravelStatusCompleted());
        User mockLoggedUser = TestHelpers.createMockNonAdminUser2();
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
        User mockLoggedUser = TestHelpers.createMockNonAdminUser1();
        Feedback mockFeedbackForPassenger = TestHelpers.createMockFeedbackForUser1ForPassenger();
        Set<Feedback> feedbackSet = mockLoggedUser.getFeedback();
        feedbackSet.add(mockFeedbackForPassenger);
        List<Feedback> feedbackList = userService.getAllFeedbackForPassenger(mockLoggedUser);

        Assertions.assertEquals(feedbackSet.size(), feedbackList.size());
    }

    /*Ilia*/
    @Test
    public void deleteUser_Should_Throw_When_UserIsNotTheSameAsLoggedUser() {
        User mockUserToBeDeleted = TestHelpers.createMockNonAdminUser1();
        User mockUserLogged = TestHelpers.createMockNonAdminUser2();

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> userService.deleteUser(mockUserToBeDeleted, mockUserLogged));
    }

    /*Ilia*/
    @Test
    public void deleteUser_Should_CallRepository_When_ValidArgumentsPassed() {
        User mockUserToBeDeleted = TestHelpers.createMockNonAdminUser1();

        userService.deleteUser(mockUserToBeDeleted, mockUserToBeDeleted);

        Mockito.verify(userRepository, Mockito.times(1))
                .updateUser(mockUserToBeDeleted);


    }

    @Test
    public void createUser_Should_Throw_When_EmailIsNotUnique() {
        User createdUser = TestHelpers.createMockNonAdminUser1();
        User userToBeCreated = TestHelpers.createMockNonAdminUser1();

        Assertions.assertThrows(DuplicateEntityException.class,
                () -> userService.createUser(userToBeCreated));
    }

    @Test
    public void createUser_Should_Throw_When_PhoneNumberIsNotUnique() {
        User createdUser = TestHelpers.createMockNonAdminUser1();
        User userToBeCreated = TestHelpers.createMockNonAdminUser1();

        Assertions.assertThrows(DuplicateEntityException.class,
                () -> userService.createUser(userToBeCreated));
    }

    @Test
    public void createUser_Should_CallRepository() {
        User userToBeCreated = TestHelpers.createMockNonAdminUser1();
        Role role = TestHelpers.createMockRoleMember();


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
    public void getUserByUsername_Should_CallRepository() {
        userService.getUserByUsername("username");

        Mockito.verify(userRepository, Mockito.times(1))
                .getUserByUsername("username");
    }

    @Test
    public void blockUser_Should_Throw_When_UserIsNotAdmin() {
        User userNotAdmin = TestHelpers.createMockNonAdminUser1();
        User userToBeBlocked = TestHelpers.createMockNonAdminUser1();

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> userService.blockUser(userToBeBlocked, userNotAdmin));

    }

    @Test
    public void blockUser_Should_CallRepository_When_ArgumentsAreValid() {
        User userAdmin = TestHelpers.createMockNonAdminUser1();
        User userToBeBlocked = TestHelpers.createMockNonAdminUser1();
        Role adminRole = TestHelpers.createMockRoleMember();
        adminRole.setRoleId(1);
        userAdmin.getRoles().add(adminRole);

        userService.blockUser(userToBeBlocked, userAdmin);

        Mockito.verify(userRepository, Mockito.times(1))
                .updateUser(userToBeBlocked);

        Assertions.assertTrue(userToBeBlocked.isBlocked());
    }

    @Test
    public void getAllUsersCount_Should_CallRepository() {
        userService.getAllUsersCount();

        Mockito.verify(userRepository, Mockito.times(1))
                .getAllUsersCount();
    }

    @Test
    public void addAvatar_Should_Throw_When_UserIsSameUser() {
        User userToBeUpdated = TestHelpers.createMockNonAdminUser1();
        User userToDoChanges = TestHelpers.createMockNonAdminUser1();
        userToDoChanges.setUserId(2);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> userService.addAvatar(userToBeUpdated, "avatarUrl", userToDoChanges));
    }

    @Test
    public void addAvatar_Should_CallRepository() {
        User userToBeUpdated = TestHelpers.createMockNonAdminUser1();
        User userToDoChanges = TestHelpers.createMockNonAdminUser1();

        userService.addAvatar(userToBeUpdated, "avatarUrl", userToDoChanges);

        Mockito.verify(userRepository, Mockito.times(1))
                .updateUser(userToBeUpdated);
    }

    @Test
    public void leaveFeedbackForPassenger_Should_Throw_When_TravelIsNotCompleted() {
        Travel travel = TestHelpers.createMockTravel1();
        User loggedUser = TestHelpers.createMockNonAdminUser1();
        User passenger = TestHelpers.createMockNonAdminUser1();
        Feedback feedback = TestHelpers.createMockFeedbackForUser1ForDriver();

        Assertions.assertThrows(InvalidOperationException.class,
                () -> userService.leaveFeedbackForPassenger(feedback, travel, loggedUser, passenger));
    }

    @Test
    public void leaveFeedbackForPassenger_Should_Throw_When_UserIsNotDriver() {
        Travel travel = TestHelpers.createMockTravel1();
        User loggedUser = TestHelpers.createMockNonAdminUser1();
        User passenger = TestHelpers.createMockNonAdminUser1();
        Feedback feedback = TestHelpers.createMockFeedbackForUser1ForDriver();
        loggedUser.setUserId(2);
        TravelStatus completedStatus = TestHelpers.createMockTravelStatusCreated();
        completedStatus.setTravelStatusId(3);
        travel.setStatus(completedStatus);
        travel.getUsersApprovedForTheTravel().add(passenger);

        Assertions.assertThrows(InvalidOperationException.class,
                () -> userService.leaveFeedbackForPassenger(feedback, travel, loggedUser, passenger));
    }

    @Test
    public void leaveFeedbackForPassenger_Should_Throw_When_PassengerNotInTheApprovedList() {
        Travel travel = TestHelpers.createMockTravel1();
        User loggedUser = TestHelpers.createMockNonAdminUser1();
        User passenger = TestHelpers.createMockNonAdminUser1();
        Feedback feedback = TestHelpers.createMockFeedbackForUser1ForDriver();
        TravelStatus completedStatus = TestHelpers.createMockTravelStatusCreated();
        completedStatus.setTravelStatusId(3);
        travel.setStatus(completedStatus);

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> userService.leaveFeedbackForPassenger(feedback, travel, loggedUser, passenger));
    }

    @Test
    public void leaveFeedbackForPassenger_Should_Throw_When_FeedbackForThisPassengerAlreadyLeft() {
        Travel travel = TestHelpers.createMockTravel1();
        TravelStatus completedStatus = TestHelpers.createMockTravelStatusCreated();
        completedStatus.setTravelStatusId(3);
        travel.setStatus(completedStatus);
        User loggedUser = TestHelpers.createMockNonAdminUser1();
        User passenger = TestHelpers.createMockNonAdminUser1();
        travel.getUsersApprovedForTheTravel().add(passenger);
        Feedback feedback = TestHelpers.createMockFeedbackForUser1ForPassenger();
        Set<Feedback> feedbackSet = passenger.getFeedback();
        feedbackSet.add(feedback);

        Mockito.when(feedbackService.createFeedback(feedback))
                .thenReturn(feedback);

        Assertions.assertThrows(InvalidOperationException.class,
                () -> userService.leaveFeedbackForPassenger(feedback, travel, loggedUser, passenger));
    }

    @Test
    public void leaveFeedbackForPassenger_Should_CallRepositoryWhenALlArgumentsAreValid() {
        Travel travel = TestHelpers.createMockTravel1();
        TravelStatus completedStatus = TestHelpers.createMockTravelStatusCreated();
        completedStatus.setTravelStatusId(3);
        travel.setStatus(completedStatus);
        User loggedUser = TestHelpers.createMockNonAdminUser1();
        User passenger = TestHelpers.createMockNonAdminUser1();
        travel.getUsersApprovedForTheTravel().add(passenger);
        Feedback feedback = TestHelpers.createMockFeedbackForUser1ForPassenger();

        Mockito.when(feedbackService.createFeedback(feedback))
                .thenReturn(feedback);

        userService.leaveFeedbackForPassenger(feedback, travel, loggedUser, passenger);

        Mockito.verify(userRepository, Mockito.times(1))
                .updateUser(passenger);
    }

    @Test
    public void verifyEmail_Should_CallRepository(){
        User userToBeVerified = TestHelpers.createMockNonAdminUser1();

        userService.verifyEmail(userToBeVerified);

        Mockito.verify(userRepository, Mockito.times(1))
                .updateUser(userToBeVerified);
    }

    @Test
    public void updateUser_Should_Throw_When_NotSameUser(){
        User userToBeVerified = TestHelpers.createMockNonAdminUser1();
        User notSameUser = TestHelpers.createMockNonAdminUser2();

        Assertions.assertThrows(UnauthorizedOperationException.class, () -> userService.updateUser(userToBeVerified, notSameUser));
    }

    @Test
    public void updateUser_Should_Throw_When_NewEmailAlreadyExists(){
        User userToBeVerified = TestHelpers.createMockNonAdminUser1();
        User userWithSameEmail = TestHelpers.createMockNonAdminUser2();

        Mockito.when(userRepository.getUserByEmail(userToBeVerified.getEmail()))
                .thenReturn(userWithSameEmail);

        Assertions.assertThrows(DuplicateEntityException.class,
                ()-> userService.updateUser(userToBeVerified, userToBeVerified));
    }

    /*TODO - to check with Ilia and Plamen why, in their opinion this method does not work.*/
    @Test
    public void updateUser_Should_Throw_When_NewPhoneAlreadyExists(){
        User userToBeVerified = TestHelpers.createMockNonAdminUser1();
        User userWithSamePhone = TestHelpers.createMockNonAdminUser2();

        Mockito.when(userRepository.getUserByEmail(userToBeVerified.getEmail()))
                .thenReturn(userToBeVerified);

        Mockito.when(userRepository.getUserByPhoneNumber(userToBeVerified.getPhoneNumber()))
                .thenReturn(userWithSamePhone);

        Assertions.assertThrows(DuplicateEntityException.class,
                ()-> userService.updateUser(userToBeVerified, userToBeVerified));
    }

    @Test
    public void updateUser_Should_CallRepository(){
        User userToBeUpdated = TestHelpers.createMockNonAdminUser1();

        Mockito.when(userRepository.getUserByEmail(userToBeUpdated.getEmail()))
                .thenThrow(EntityNotFoundException.class);
        Mockito.when(userRepository.getUserByPhoneNumber(userToBeUpdated.getPhoneNumber()))
                .thenThrow(EntityNotFoundException.class);

        userService.updateUser(userToBeUpdated, userToBeUpdated);
        Mockito.verify(userRepository, Mockito.times(1))
                .updateUser(userToBeUpdated);
    }


    @Test
    public void getUserByEmail_Should_CallRepository(){
        User userToBeReturned = TestHelpers.createMockNonAdminUser1();

        Mockito.when(userRepository.getUserByEmail(userToBeReturned.getEmail()))
                .thenReturn(userToBeReturned);

        userService.getUserByEmail(userToBeReturned.getEmail());

        Mockito.verify(userRepository, Mockito.times(1))
                .getUserByEmail(userToBeReturned.getEmail());
    }

    @Test
    public void unblockUser_Should_Throw_When_UserIsNotAdmin() {
        User userNotAdmin = TestHelpers.createMockNonAdminUser1();
        User userToBeUnblocked = TestHelpers.createMockNonAdminUser1();

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> userService.blockUser(userToBeUnblocked, userNotAdmin));
    }

    @Test
    public void unblockUser_Should_CallRepository() {
        User admin = TestHelpers.createMockNonAdminUser1();
        User userToBeUnblocked = TestHelpers.createMockNonAdminUser1();
        Role adminRole = TestHelpers.createMockRoleMember();
        adminRole.setRoleId(1);
        admin.getRoles().add(adminRole);

        userService.unBlockUser(userToBeUnblocked, admin);

        Mockito.verify(userRepository, Mockito.times(1))
                .updateUser(userToBeUnblocked);

        Assertions.assertFalse(userToBeUnblocked.isBlocked());
    }


    @Test
    public void getTopTwelveTravelOrganizersByRating_Should_CallRepository(){
        List<User> result = new ArrayList<>();

        Mockito.when(userRepository.getTopTwelveTravelOrganizersByRating())
                .thenReturn(result);

        userService.getTopTwelveTravelOrganizersByRating();

        Mockito.verify(userRepository, Mockito.times(1))
                .getTopTwelveTravelOrganizersByRating();
    }

    @Test
    public void deleteAvatar_Should_Throw_When_NotSameUserAndNotAdmin(){
        User userTobeUpdated = TestHelpers.createMockNonAdminUser1();
        User nonAdminUser = TestHelpers.createMockNonAdminUser2();

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> userService.deleteUser(userTobeUpdated, nonAdminUser));
    }

    @Test
    public void deleteAvatar_Should_CallRepository(){
        User userTobeUpdated = TestHelpers.createMockNonAdminUser1();

        Mockito.when(userRepository.updateUser(userTobeUpdated))
                .thenReturn(userTobeUpdated);

        userService.deleteAvatar(userTobeUpdated, userTobeUpdated);

        Mockito.verify(userRepository, Mockito.times(1))
                .updateUser(userTobeUpdated);
    }

    /*TODO - do we need to write a test for this method at all?*/
    @Test
    public void getAllFeedbackForDriver_Should_Pass(){
        User user = TestHelpers.createMockNonAdminUser1();
        Assertions.assertEquals(user.getFeedback(), user.getFeedback());
    }

}
