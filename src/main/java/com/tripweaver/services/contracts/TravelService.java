package com.tripweaver.services.contracts;

import com.tripweaver.models.Travel;
import com.tripweaver.models.filterOptions.TravelFilterOptions;
import com.tripweaver.models.User;
import java.util.List;

public interface TravelService {

    /*ToDo Ilia*/
    Travel createTravel(Travel travel,User creator);

    /*ToDo Plamen*/
    Travel cancelTravel(Travel travel, User loggedUser);

    /*ToDo Yuli - DONE*/
    Travel completeTravel(Travel travel, User loggedUser);

    /*ToDo Ilia*/
    List<Travel> getTravelsByDriver(User driver, User loggedUser, TravelFilterOptions travelFilterOptions);

    /*ToDo Plamen*/
    List<Travel> getTravelsByPassenger(User passenger, User loggedUser,TravelFilterOptions travelFilterOptions);

    /*ToDo Yuli - DONE*/
    Travel getTravelById(int travelId);

    /*ToDo Ilia*/
    List<Travel> getAllTravels(TravelFilterOptions travelFilterOptions);

    /*ToDo Plamen*/
    Travel applyForATrip(User userToApply, Travel travelToApplyFor);

    /*ToDo Yuli - DONE*/
    Travel approvePassenger(User userToApprove, Travel travel);

    /*ToDo Ilia*/
    Travel declinePassenger(User userToBeDeclined, Travel travel, User userLoggedIn);

}
