package com.tripweaver.services;

import com.tripweaver.exceptions.InvalidOperationException;
import com.tripweaver.exceptions.UnauthorizedOperationException;
import com.tripweaver.helpers.TestHelpers;
import com.tripweaver.models.Travel;
import com.tripweaver.models.TravelStatus;
import com.tripweaver.models.User;
import com.tripweaver.models.filterOptions.TravelFilterOptions;
import com.tripweaver.exceptions.EntityNotFoundException;
import com.tripweaver.repositories.contracts.TravelRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import com.tripweaver.services.contracts.BingMapService;
import com.tripweaver.services.contracts.TravelStatusService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;

import static com.tripweaver.services.helpers.ConstantHelper.*;

import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class TravelServiceTests {

    @Mock
    TravelRepository travelRepository;

    @Mock
    TravelStatusService travelStatusService;
    @Mock
    BingMapService bingMapService;
    @InjectMocks
    TravelServiceImpl travelService;

    /*Ilia*/
    @Test
    public void createTravel_Should_Throw_When_CreatorUserIsBlocked() {
        Travel mockTravel = TestHelpers.createMockTravel1();
        User mockCreator = TestHelpers.createMockNonAdminUser1();
        mockCreator.setBlocked(true);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> travelService.createTravel(mockTravel, mockCreator));
    }

    /*Ilia*/
    @Test
    public void createTravel_Should_Throw_When_CreatorUserIsNotVerified() {
        Travel mockTravel = TestHelpers.createMockTravel1();
        User mockCreator = TestHelpers.createMockNonAdminUser1();
        mockCreator.setVerified(false);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> travelService.createTravel(mockTravel, mockCreator));
    }

    /*Ilia*/
    @Test
    public void createTravel_Should_Throw_When_TravelDepartureTimeBeforeCurrentMoment() {
        Travel mockTravel = TestHelpers.createMockTravel1();
        mockTravel.setDepartureTime(LocalDateTime.now().minusHours(2));
        User mockCreator = TestHelpers.createMockNonAdminUser1();

        Assertions.assertThrows(InvalidOperationException.class,
                () -> travelService.createTravel(mockTravel, mockCreator));
    }

    /*Ilia*/
    @Test
    public void createTravel_Should_CallRepository_When_ValidArgumentsPassed() {
        Travel mockTravel = TestHelpers.createMockTravel1();
        User mockCreator = TestHelpers.createMockNonAdminUser1();
        TravelStatus mockTravelStatus = TestHelpers.createMockTravelStatusCreated();

        HashMap<String, String> mockCoordinates = new HashMap<>();
        mockCoordinates.put(KEY_CITY, "MockCity");
        mockCoordinates.put(KEY_COORDINATES, "MockCoordinates");
        HashMap<String, Integer> mockDistanceAndDuration = new HashMap<>();
        mockDistanceAndDuration.put(KEY_TRAVEL_DISTANCE, 30);
        mockDistanceAndDuration.put(KEY_TRAVEL_DURATION, 30);

        Mockito.when(travelStatusService.getStatusById(Mockito.anyInt()))
                .thenReturn(mockTravelStatus);
        Mockito.when(bingMapService.getCoordinatesAndValidCityName(Mockito.anyString()))
                .thenReturn(mockCoordinates);
        Mockito.when(bingMapService.calculateDistanceAndDuration(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(mockDistanceAndDuration);

        travelService.createTravel(mockTravel, mockCreator);

        Mockito.verify(travelRepository, Mockito.times(1))
                .createTravel(mockTravel);
    }

    /*Ilia*/
    @Test
    public void getTravelsByDriver_Should_Throw_When_LoggedUserIsNotTheDriver() {
        User mockLoggedUser = TestHelpers.createMockNonAdminUser1();
        User mockDriver = TestHelpers.createMockNonAdminUser2();
        TravelFilterOptions mockTravelFilterOptions = TestHelpers.createMockTravelFilterOptions();

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> travelService.getTravelsByDriver(mockDriver, mockLoggedUser, mockTravelFilterOptions));
    }

    /*Ilia*/
    @Test
    public void getTravelsByDriver_Should_CallRepository_When_WhenValidArgumentsPassed() {
        User mockLoggedUser = TestHelpers.createMockNonAdminUser1();
        TravelFilterOptions mockTravelFilterOptions = TestHelpers.createMockTravelFilterOptions();

        travelService.getTravelsByDriver(mockLoggedUser, mockLoggedUser, mockTravelFilterOptions);

        Mockito.verify(travelRepository, Mockito.times(1))
                .getAllTravels(mockTravelFilterOptions);

    }

    /*Ilia*/
    @Test
    public void getAllTravels_Should_CallRepository() {
        TravelFilterOptions mockTravelFilterOptions = TestHelpers.createMockTravelFilterOptions();

        travelService.getAllTravels(mockTravelFilterOptions);

        Mockito.verify(travelRepository, Mockito.times(1))
                .getAllTravels(mockTravelFilterOptions);

    }

    /*Ilia*/
    @Test
    public void declinePassenger_Should_Throw_When_UserToBeDeclinedIsNotDriverOrSameUser() {
        Travel mockTravel = TestHelpers.createMockTravel2();
        User mockUserToBeDeclined = TestHelpers.createMockNonAdminUser1();
        User mockLoggedUser = TestHelpers.createMockNonAdminUser1();
        mockLoggedUser.setUserId(10);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> travelService.declinePassenger(mockUserToBeDeclined, mockTravel, mockLoggedUser));
    }

    /*Ilia*/
    @Test
    public void declinePassenger_Should_Throw_When_TravelNotOpenForApplication() {
        Travel mockTravel = TestHelpers.createMockTravel2();
        mockTravel.setStatus(TestHelpers.createMockTravelStatusCanceled());
        User mockUserToBeDeclined = TestHelpers.createMockNonAdminUser1();

        Assertions.assertThrows(InvalidOperationException.class,
                () -> travelService.declinePassenger(mockUserToBeDeclined, mockTravel, mockUserToBeDeclined));
    }

    /*Ilia*/
    @Test
    public void declinePassenger_Should_Throw_When_UserToBeDeclinedNotInTravelLists() {
        Travel mockTravel = TestHelpers.createMockTravel2();
        mockTravel.setUsersApprovedForTheTravel(new HashSet<>());
        mockTravel.setUsersAppliedForTheTravel(new HashSet<>());
        User mockUserToBeDeclined = TestHelpers.createMockNonAdminUser1();

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> travelService.declinePassenger(mockUserToBeDeclined, mockTravel, mockUserToBeDeclined));
    }

    /*Ilia*/
    @Test
    public void declinePassenger_Should_CallRepository_When_WhenValidArgumentsPassed() {
        Travel mockTravel = TestHelpers.createMockTravel2();
        User mockUserToBeDeclined = TestHelpers.createMockNonAdminUser1();

        travelService.declinePassenger(mockUserToBeDeclined, mockTravel, mockTravel.getDriver());

        Mockito.verify(travelRepository, Mockito.times(1))
                .updateTravel(mockTravel);
    }


    @Test
    public void cancelTravel_Should_Throw_When_UserIsNotDriver() {
        User loggedUser = TestHelpers.createMockNonAdminUser1();
        Travel travel = TestHelpers.createMockTravel1();

        loggedUser.setUserId(2);

        Assertions.assertThrows(InvalidOperationException.class,
                () -> travelService.cancelTravel(travel, loggedUser));
    }

    @Test
    public void cancelTravel_Should_Throw_When_TravelIsCancelled() {
        User loggedUser = TestHelpers.createMockNonAdminUser1();
        Travel travel = TestHelpers.createMockTravel1();
        TravelStatus statusCancelled = TestHelpers.createMockTravelStatusCreated();
        statusCancelled.setTravelStatusId(3);
        travel.setStatus(statusCancelled);


        Assertions.assertThrows(InvalidOperationException.class,
                () -> travelService.cancelTravel(travel, loggedUser));
    }

    @Test
    public void cancelTravel_Should_Cancel_When_ArgumentsAreValid() {
        User loggedUser = TestHelpers.createMockNonAdminUser1();
        Travel travel = TestHelpers.createMockTravel1();
        travel.setDriver(loggedUser);

        TravelStatus cancelledStatus = TestHelpers.createMockTravelStatusCreated();
        cancelledStatus.setTravelStatusId(2);

        Mockito.when(travelStatusService.getStatusById(2))
                .thenReturn(cancelledStatus);

        travelService.cancelTravel(travel, loggedUser);

        Assertions.assertEquals(cancelledStatus, travel.getStatus());
    }

    @Test
    public void getAllTravelsCount_Should_CallRepository() {
        travelService.getAllTravelsCount();

        Mockito.verify(travelRepository, Mockito.times(1))
                .getAllTravelsCount();
    }

    @Test
    public void getTravelsByPassenger_Should_CallRepository_When_ArgumentsAreValid() {
        User loggedUser = TestHelpers.createMockNonAdminUser1();
        User passenger = TestHelpers.createMockNonAdminUser1();
        TravelFilterOptions travelFilterOptions = TestHelpers.createMockTravelFilterOptions();

        travelService.getTravelsByPassenger(loggedUser, passenger, travelFilterOptions);

        Mockito.verify(travelRepository, Mockito.times(1))
                .getAllTravels(travelFilterOptions);
    }

    @Test
    public void getTravelsByPassenger_Should_Throw_When_NotSameUser() {
        User loggedUser = TestHelpers.createMockNonAdminUser1();
        User passenger = TestHelpers.createMockNonAdminUser1();
        passenger.setUserId(2);
        TravelFilterOptions travelFilterOptions = TestHelpers.createMockTravelFilterOptions();

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> travelService.getTravelsByPassenger(passenger, loggedUser, travelFilterOptions));
    }

    @Test
    public void applyForTrip_Should_Throw_When_UserIsNotVerified() {
        User loggedUser = TestHelpers.createMockNonAdminUser1();
        Travel travel = TestHelpers.createMockTravel1();
        loggedUser.setVerified(false);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> travelService.applyForATrip(loggedUser, travel));
    }

    @Test
    public void applyForTrip_Should_Throw_When_UserIsBlocked() {
        User loggedUser = TestHelpers.createMockNonAdminUser1();
        Travel travel = TestHelpers.createMockTravel1();
        loggedUser.setBlocked(true);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> travelService.applyForATrip(loggedUser, travel));
    }

    @Test
    public void applyForTrip_Should_Throw_When_UserAlreadyApplied() {
        User loggedUser = TestHelpers.createMockNonAdminUser1();
        Travel travel = TestHelpers.createMockTravel1();

        Set<User> usersAppliedForTheTrip = new HashSet<>();
        usersAppliedForTheTrip.add(loggedUser);

        travel.setUsersAppliedForTheTravel(usersAppliedForTheTrip);

        Assertions.assertThrows(InvalidOperationException.class,
                () -> travelService.applyForATrip(loggedUser, travel));
    }

    @Test
    public void applyForTrip_Should_Throw_When_TravelIsCancelled() {
        User loggedUser = TestHelpers.createMockNonAdminUser1();
        Travel travel = TestHelpers.createMockTravel1();

        TravelStatus statusCancel = TestHelpers.createMockTravelStatusCreated();
        statusCancel.setTravelStatusId(2);
        travel.setStatus(statusCancel);

        Assertions.assertThrows(InvalidOperationException.class,
                () -> travelService.applyForATrip(loggedUser, travel));
    }

    @Test
    public void applyForTrip_Should_Throw_When_UserIsDriver() {
        User loggedUser = TestHelpers.createMockNonAdminUser1();
        Travel travel = TestHelpers.createMockTravel1();
        travel.setDriver(loggedUser);

        Assertions.assertThrows(InvalidOperationException.class,
                () -> travelService.applyForATrip(loggedUser, travel));
    }

    @Test
    public void applyForTrip_Should_CallRepository() {
        User loggedUser = TestHelpers.createMockNonAdminUser1();
        Travel travel = TestHelpers.createMockTravel1();
        loggedUser.setUserId(2);

        travelService.applyForATrip(loggedUser, travel);

        Mockito.verify(travelRepository, Mockito.times(1))
                .updateTravel(travel);
    }


}
