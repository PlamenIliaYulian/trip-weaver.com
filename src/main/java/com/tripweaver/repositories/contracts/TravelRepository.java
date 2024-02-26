package com.tripweaver.repositories.contracts;

import com.tripweaver.models.Travel;
import com.tripweaver.models.filterOptions.TravelFilterOptions;
import com.tripweaver.models.User;

import java.util.List;

public interface TravelRepository {

    Travel createTravel(Travel travel);

    Travel updateTravel(Travel travel);

    Travel getTravelById(int travelId);

    List<Travel> getTravelsByDriver(User driver, TravelFilterOptions travelFilterOptions);

    List<Travel> getTravelsByPassenger(User passenger);

    List<Travel> getAllTravels(TravelFilterOptions travelFilterOptions);

}
