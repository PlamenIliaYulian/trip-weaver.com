package com.tripweaver.services.contracts;

import com.tripweaver.models.Travel;
import com.tripweaver.models.User;
import com.tripweaver.models.filterOptions.TravelFilterOptions;

import java.util.List;

public interface TravelService {

    Travel createTravel(Travel travel, User creator);

    Travel cancelTravel(Travel travel, User loggedUser);

    Travel completeTravel(Travel travel, User loggedUser);

    List<Travel> getTravelsByDriver(User driver, User loggedUser, TravelFilterOptions travelFilterOptions);

    List<Travel> getTravelsByPassenger(User passenger, User loggedUser, TravelFilterOptions travelFilterOptions);

    Travel getTravelById(int travelId);

    List<Travel> getTravelsAsAppliedPassenger(User loggedUser, User passengerAppliedToTravels);

    List<Travel> getAllTravels(TravelFilterOptions travelFilterOptions);

    Travel applyForATrip(User userToApply, Travel travelToApplyFor);

    Travel approvePassenger(User userToBeApproved, User loggedUser, Travel travel);

    Travel declinePassenger(User userToBeDeclined, Travel travel, User userLoggedIn);

    Long getAllTravelsCount();
}
