package com.tripweaver.services;

import com.tripweaver.repositories.contracts.FeedbackRepository;
import com.tripweaver.repositories.contracts.TravelStatusRepository;
import com.tripweaver.services.contracts.TravelStatusService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

@ExtendWith(MockitoExtension.class)
public class TravelStatusServiceTests {

    @Mock
    TravelStatusRepository travelStatusRepository;

    @InjectMocks
    TravelStatusServiceImpl travelStatusService;

    @Test
    public void getStatusById_Should_CallRepository() {
        travelStatusService.getStatusById(Mockito.anyInt());

        Mockito.verify(travelStatusRepository, Mockito.times(1))
                .getStatusById(Mockito.anyInt());
    }
}
