package com.tripweaver.services.contracts;

import com.tripweaver.models.Travel;
import com.tripweaver.models.TravelFilterOptions;
import com.tripweaver.models.User;
import java.util.List;

public interface TravelService {

    /*ToDo Ilia*/
    Travel createTravel(Travel travel);

    /*ToDo Plamen*/
    Travel cancelTravel(Travel travel);

    /*ToDo Yuli - DONE*/
    Travel completeTravel(Travel travel);

    /*ToDo Ilia*/
    List<Travel> getTravelsByDriver(User driver, User loggedUser);

    /*ToDo Plamen*/
    List<Travel> getTravelsByPassenger(User passenger, User loggedUser);

    /*ToDo Yuli*/
    Travel getTravelById(int travelId);

    /*ToDo Ilia*/
    List<Travel> getAllTravels(TravelFilterOptions travelFilterOptions);

    /*ToDo Plamen*/
    Travel applyForATrip(User userToApply, Travel travelToApplyFor);

    /*ToDo Yuli*/
    Travel approvePassenger(User userToApprove, Travel travel);

    /*ToDo Ilia*/
    Travel declinePassenger(User userToBeDeclined, Travel travel);

}
