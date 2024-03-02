package com.tripweaver.services;

import com.tripweaver.exceptions.UnauthorizedOperationException;
import com.tripweaver.helpers.TestHelpers;
import com.tripweaver.models.Travel;
import com.tripweaver.models.User;
import com.tripweaver.repositories.contracts.TravelRepository;
import com.tripweaver.services.contracts.TravelService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TravelServiceTests {


    @Mock
    TravelRepository travelRepository;

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
}
