package com.tripweaver.services;

import com.tripweaver.models.TravelStatus;
import com.tripweaver.repositories.contracts.TravelStatusRepository;
import com.tripweaver.services.contracts.TravelStatusService;
import org.springframework.stereotype.Service;

@Service
public class TravelStatusServiceImpl implements TravelStatusService {

    private final TravelStatusRepository travelStatusRepository;

    public TravelStatusServiceImpl(TravelStatusRepository travelStatusRepository) {
        this.travelStatusRepository = travelStatusRepository;
    }

    @Override
    public TravelStatus getStatusById(int travelStatusId) {
        return travelStatusRepository.getStatusById(travelStatusId);
    }
}
