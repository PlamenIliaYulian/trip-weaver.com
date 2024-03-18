package com.tripweaver.repositories.contracts;

import com.tripweaver.models.Travel;
import com.tripweaver.models.User;
import com.tripweaver.models.filterOptions.TravelFilterOptions;

import java.util.List;

public interface TravelRepository {

    Travel createTravel(Travel travel);

    Travel updateTravel(Travel travel);

    Travel getTravelById(int travelId);

    List<Travel> getTravelsAsAppliedPassenger(User appliedPassenger);

    List<Travel> getAllTravels(TravelFilterOptions travelFilterOptions);

    Long getAllTravelsCount();
}
