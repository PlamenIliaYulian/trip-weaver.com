package com.tripweaver.services;

import com.tripweaver.repositories.contracts.TravelStatusRepository;
import com.tripweaver.services.contracts.TravelStatusService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TravelStatusServiceTests {

    @Mock
    TravelStatusRepository travelStatusRepository;

    @InjectMocks
    TravelStatusServiceImpl travelStatusService;
}
