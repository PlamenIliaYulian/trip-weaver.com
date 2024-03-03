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
import com.tripweaver.repositories.contracts.UserRepository;
import com.tripweaver.services.contracts.FeedbackService;
import com.tripweaver.services.contracts.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock
    UserRepository userRepository;
    @Mock
    FeedbackService feedbackService;

    @InjectMocks
    UserServiceImpl userService;

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
    public void getAllFeedbackForPassenger_Should_ReturnListOfFeedback_When_Called(){
        User mockLoggedUser = TestHelpers.createMockNonAdminUser1$Ilia();
        Feedback mockFeedbackForPassenger = TestHelpers.createMockFeedbackForUser1ForPassenger$Ilia();
        Set<Feedback> feedbackSet = mockLoggedUser.getFeedback();
        feedbackSet.add(mockFeedbackForPassenger);
        List<Feedback> feedbackList = userService.getAllFeedbackForPassenger(mockLoggedUser);

        Assertions.assertEquals(feedbackSet.size(), feedbackList.size());
    }
}
