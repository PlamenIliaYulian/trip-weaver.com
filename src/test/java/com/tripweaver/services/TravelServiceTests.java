package com.tripweaver.services;

import com.tripweaver.exceptions.InvalidOperationException;
import com.tripweaver.exceptions.UnauthorizedOperationException;
import com.tripweaver.helpers.TestHelpers;
import com.tripweaver.models.Travel;
import com.tripweaver.models.TravelStatus;
import com.tripweaver.models.User;
import com.tripweaver.repositories.contracts.TravelRepository;
import com.tripweaver.services.contracts.BingMapService;
import com.tripweaver.services.contracts.TravelService;
import com.tripweaver.services.contracts.TravelStatusService;
import com.tripweaver.services.helpers.ConstantHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashMap;

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

    @Test
    public void createTravel_Should_Throw_When_CreatorUserIsBlocked() {
        Travel mockTravel = TestHelpers.createMockTravel1$Ilia();
        User mockCreator = TestHelpers.createMockNonAdminUser1$Ilia();
        mockCreator.setBlocked(true);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                ()-> travelService.createTravel(mockTravel, mockCreator));
    }
    @Test
    public void createTravel_Should_Throw_When_CreatorUserIsNotVerified() {
        Travel mockTravel = TestHelpers.createMockTravel1$Ilia();
        User mockCreator = TestHelpers.createMockNonAdminUser1$Ilia();
        mockCreator.setVerified(false);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                ()-> travelService.createTravel(mockTravel, mockCreator));
    }
    @Test
    public void createTravel_Should_Throw_When_TravelDepartureTimeBeforeCurrentMoment() {
        Travel mockTravel = TestHelpers.createMockTravel1$Ilia();
        mockTravel.setDepartureTime(LocalDateTime.now().minusHours(2));
        User mockCreator = TestHelpers.createMockNonAdminUser1$Ilia();

        Assertions.assertThrows(InvalidOperationException.class,
                ()-> travelService.createTravel(mockTravel, mockCreator));
    }
    @Test
    public void createTravel_Should_CallRepository_WhenValidArgumentsPassed(){
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

        Mockito.verify(travelRepository,Mockito.times(1))
                .createTravel(mockTravel);
    }

}
