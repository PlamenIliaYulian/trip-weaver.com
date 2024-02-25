package com.tripweaver.repositories.contracts;

import com.tripweaver.models.Travel;
import com.tripweaver.models.TravelFilterOptions;
import com.tripweaver.models.User;

import java.util.List;

public interface TravelRepository {

    Travel createTravel(Travel travel);

    Travel cancelTravel(Travel travel);

    Travel completeTravel(Travel travel);

    Travel updateTravel(Travel travel);

    Travel getTravelById(int travelId);

    List<Travel> getTravelsByDriver(User driver);

    List<Travel> getTravelsByPassenger(User passenger);

    List<Travel> getAllTravels(TravelFilterOptions travelFilterOptions);

    Travel applyForATrip(User userToApply, Travel travelToApplyFor);

    Travel approvePassenger(User userToApprove, Travel travel);

    Travel declinePassenger(User userToBeDeclined, Travel travel);

    Travel updateTravel(Travel travel);



}
