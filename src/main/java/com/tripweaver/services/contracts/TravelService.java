package com.tripweaver.services.contracts;

import com.tripweaver.models.Travel;
import com.tripweaver.models.filterOptions.TravelFilterOptions;
import com.tripweaver.models.User;
import java.util.List;

public interface TravelService {

    /*Ilia*/
    Travel createTravel(Travel travel,User creator);

    /*Plamen*/
    Travel cancelTravel(Travel travel, User loggedUser);

    /*Yuli - DONE*/
    Travel completeTravel(Travel travel, User loggedUser);

    /*Ilia*/
    List<Travel> getTravelsByDriver(User driver, User loggedUser, TravelFilterOptions travelFilterOptions);

    /*Plamen*/
    List<Travel> getTravelsByPassenger(User passenger, User loggedUser,TravelFilterOptions travelFilterOptions);

    /*Yuli - DONE*/
    Travel getTravelById(int travelId);

    /*Ilia*/
    List<Travel> getAllTravels(TravelFilterOptions travelFilterOptions);

    /*Plamen*/
    Travel applyForATrip(User userToApply, Travel travelToApplyFor);

    /*Yuli - DONE*/
    Travel approvePassenger(User userToBeApproved, User loggedUser, Travel travel);

    /*Ilia*/
    Travel declinePassenger(User userToBeDeclined, Travel travel, User userLoggedIn);
    /*Plamen*/
    Long getAllTravelsCount();
}
