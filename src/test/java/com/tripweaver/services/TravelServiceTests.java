package com.tripweaver.services;

import com.tripweaver.exceptions.UnauthorizedOperationException;
import com.tripweaver.helpers.TestHelpers;
import com.tripweaver.models.Travel;
import com.tripweaver.models.TravelStatus;
import com.tripweaver.models.User;
import com.tripweaver.models.filterOptions.TravelFilterOptions;
import com.tripweaver.exceptions.EntityNotFoundException;
import com.tripweaver.exceptions.InvalidOperationException;
import com.tripweaver.exceptions.UnauthorizedOperationException;
import com.tripweaver.helpers.TestHelpers;
import com.tripweaver.models.Travel;
import com.tripweaver.models.TravelStatus;
import com.tripweaver.models.User;
import com.tripweaver.models.filterOptions.TravelFilterOptions;
import com.tripweaver.repositories.contracts.TravelRepository;
import com.tripweaver.services.contracts.TravelService;
import com.tripweaver.services.helpers.PermissionHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import com.tripweaver.services.contracts.BingMapService;
import com.tripweaver.services.contracts.TravelStatusService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;

import static com.tripweaver.services.helpers.ConstantHelper.*;

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
        Travel mockTravel = TestHelpers.createMockTravel1$Ilia();
        User mockCreator = TestHelpers.createMockNonAdminUser1$Ilia();
        mockCreator.setBlocked(true);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> travelService.createTravel(mockTravel, mockCreator));
    }

    /*Ilia*/
    @Test
    public void createTravel_Should_Throw_When_CreatorUserIsNotVerified() {
        Travel mockTravel = TestHelpers.createMockTravel1$Ilia();
        User mockCreator = TestHelpers.createMockNonAdminUser1$Ilia();
        mockCreator.setVerified(false);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> travelService.createTravel(mockTravel, mockCreator));
    }

    /*Ilia*/
    @Test
    public void createTravel_Should_Throw_When_TravelDepartureTimeBeforeCurrentMoment() {
        Travel mockTravel = TestHelpers.createMockTravel1$Ilia();
        mockTravel.setDepartureTime(LocalDateTime.now().minusHours(2));
        User mockCreator = TestHelpers.createMockNonAdminUser1$Ilia();

        Assertions.assertThrows(InvalidOperationException.class,
                () -> travelService.createTravel(mockTravel, mockCreator));
    }

    /*Ilia*/
    @Test
    public void createTravel_Should_CallRepository_When_ValidArgumentsPassed() {
        Travel mockTravel = TestHelpers.createMockTravel1$Ilia();
        User mockCreator = TestHelpers.createMockNonAdminUser1$Ilia();
        TravelStatus mockTravelStatus = TestHelpers.createMockTravelStatusCreated$Ilia();

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
        User mockLoggedUser = TestHelpers.createMockNonAdminUser1$Ilia();
        User mockDriver = TestHelpers.createMockNonAdminUser2$Ilia();
        TravelFilterOptions mockTravelFilterOptions = TestHelpers.createMockTravelFilterOptions$Ilia();

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> travelService.getTravelsByDriver(mockDriver, mockLoggedUser, mockTravelFilterOptions));
    }

    /*Ilia*/
    @Test
    public void getTravelsByDriver_Should_CallRepository_When_WhenValidArgumentsPassed() {
        User mockLoggedUser = TestHelpers.createMockNonAdminUser1$Ilia();
        TravelFilterOptions mockTravelFilterOptions = TestHelpers.createMockTravelFilterOptions$Ilia();

        travelService.getTravelsByDriver(mockLoggedUser, mockLoggedUser, mockTravelFilterOptions);

        Mockito.verify(travelRepository, Mockito.times(1))
                .getAllTravels(mockTravelFilterOptions);

    }

    /*Ilia*/
    @Test
    public void getAllTravels_Should_CallRepository() {
        TravelFilterOptions mockTravelFilterOptions = TestHelpers.createMockTravelFilterOptions$Ilia();

        travelService.getAllTravels(mockTravelFilterOptions);

        Mockito.verify(travelRepository, Mockito.times(1))
                .getAllTravels(mockTravelFilterOptions);

    }

    /*Ilia*/
    @Test
    public void declinePassenger_Should_Throw_When_UserToBeDeclinedIsNotDriverOrSameUser() {
        Travel mockTravel = TestHelpers.createMockTravel2$Ilia();
        User mockUserToBeDeclined = TestHelpers.createMockNonAdminUser1$Ilia();
        User mockLoggedUser = TestHelpers.createMockNonAdminUser1$Ilia();
        mockLoggedUser.setUserId(10);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> travelService.declinePassenger(mockUserToBeDeclined, mockTravel, mockLoggedUser));
    }

    /*Ilia*/
    @Test
    public void declinePassenger_Should_Throw_When_TravelNotOpenForApplication() {
        Travel mockTravel = TestHelpers.createMockTravel2$Ilia();
        mockTravel.setStatus(TestHelpers.createMockTravelStatusCanceled$Ilia());
        User mockUserToBeDeclined = TestHelpers.createMockNonAdminUser1$Ilia();

        Assertions.assertThrows(InvalidOperationException.class,
                () -> travelService.declinePassenger(mockUserToBeDeclined, mockTravel, mockUserToBeDeclined));
    }

    /*Ilia*/
    @Test
    public void declinePassenger_Should_Throw_When_UserToBeDeclinedNotInTravelLists() {
        Travel mockTravel = TestHelpers.createMockTravel2$Ilia();
        mockTravel.setUsersApprovedForTheTravel(new HashSet<>());
        mockTravel.setUsersAppliedForTheTravel(new HashSet<>());
        User mockUserToBeDeclined = TestHelpers.createMockNonAdminUser1$Ilia();

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> travelService.declinePassenger(mockUserToBeDeclined, mockTravel, mockUserToBeDeclined));
    }

    /*Ilia*/
    @Test
    public void declinePassenger_Should_CallRepository_When_WhenValidArgumentsPassed() {
        Travel mockTravel = TestHelpers.createMockTravel2$Ilia();
        User mockUserToBeDeclined = TestHelpers.createMockNonAdminUser1$Ilia();

        travelService.declinePassenger(mockUserToBeDeclined, mockTravel, mockTravel.getDriver());

        Mockito.verify(travelRepository, Mockito.times(1))
                .updateTravel(mockTravel);
    }


    @Test
    public void cancelTravel_Should_Throw_When_UserIsNotDriver(){
        User loggedUser = TestHelpers.createMockUserPlamen();
        Travel travel = TestHelpers.createMockTravelPlamen();

        loggedUser.setUserId(2);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                ()-> travelService.cancelTravel(travel, loggedUser));
    }

    @Test
    public void cancelTravel_Should_Throw_When_TravelIsCancelled(){
        User loggedUser = TestHelpers.createMockUserPlamen();
        Travel travel = TestHelpers.createMockTravelPlamen();
        TravelStatus travelStatus = TestHelpers.createMockTravelStatusPlamen();
        travelStatus.setTravelStatusId(3);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                ()-> travelService.cancelTravel(travel, loggedUser));
    }

    @Test
    public void cancelTravel_Should_Cancel_When_ArgumentsAreValid(){
        User loggedUser = TestHelpers.createMockUserPlamen();
        Travel travel = TestHelpers.createMockTravelPlamen();
        travel.setDriver(loggedUser);

        TravelStatus cancelledStatus = TestHelpers.createMockTravelStatusPlamen();
        cancelledStatus.setTravelStatusId(2);

        travelService.cancelTravel(travel, loggedUser);

        Assertions.assertEquals(cancelledStatus, travel.getStatus());
    }

    @Test
    public void getAllTravelsCount_Should_CallRepository(){
        travelService.getAllTravelsCount();

        Mockito.verify(travelRepository, Mockito.times(1))
                .getAllTravelsCount();
    }

    @Test
    public void getTravelsByPassenger_Should_CallRepository_When_ArgumentsAreValid(){
        User loggedUser = TestHelpers.createMockUserPlamen();
        User passenger = TestHelpers.createMockUserPlamen();
        TravelFilterOptions travelFilterOptions = TestHelpers.createMockTravelFilterOptionsPlamen();

        travelService.getTravelsByPassenger(loggedUser, passenger,travelFilterOptions);

        Mockito.verify(travelRepository, Mockito.times(1))
                .getAllTravels(travelFilterOptions);
    }
}
