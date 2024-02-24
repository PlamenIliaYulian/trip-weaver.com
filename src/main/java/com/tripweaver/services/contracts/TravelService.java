package com.tripweaver.services.contracts;

import com.tripweaver.models.Travel;
import com.tripweaver.models.TravelFilterOptions;
import com.tripweaver.models.User;
import java.util.List;

public interface TravelService {

    Travel createTravel(Travel travel);

    Travel cancelTravel(Travel travel);

    Travel completeTravel(Travel travel);

    List<Travel> getTravelsByDriver(User driver, User loggedUser);

    List<Travel> getTravelsByPassenger(User passenger, User loggedUser);

    Travel getTravelById(int travelId);

    List<Travel> getAllTravels(TravelFilterOptions travelFilterOptions);

    Travel applyForATrip(User userToApply, Travel travelToApplyFor);

    Travel approvePassenger(User userToApprove, Travel travel);

    Travel declinePassenger(User userToBeDeclined, Travel travel);

}
