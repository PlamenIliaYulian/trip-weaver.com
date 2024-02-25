package com.tripweaver.repositories.contracts;

import com.tripweaver.models.TravelStatus;

public interface TravelStatusRepository {

    TravelStatus getStatusById(int travelStatusId);
}
