package com.tripweaver.services;

import com.tripweaver.exceptions.EntityNotFoundException;
import com.tripweaver.exceptions.UnauthorizedOperationException;
import com.tripweaver.exceptions.InvalidOperationException;
import com.tripweaver.exceptions.UnauthorizedOperationException;
import com.tripweaver.models.Travel;
import com.tripweaver.models.TravelFilterOptions;
import com.tripweaver.models.TravelStatus;
import com.tripweaver.models.User;
import com.tripweaver.repositories.contracts.TravelRepository;
import com.tripweaver.services.contracts.TravelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Autowired;
import com.tripweaver.services.contracts.TravelStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class TravelServiceImpl implements TravelService {

    public static final String UNAUTHORIZED_OPERATION_BLOCKED = "Unauthorized operation. User blocked.";
    public static final String UNAUTHORIZED_OPERATION_NOT_VERIFIED = "Unauthorized operation. User not verified.";
    public static final String UNAUTHORIZED_OPERATION_NOT_DRIVER = "Unauthorized operation. User not driver of the travel.";
    public static final String USER_NOT_IN_TRAVEL_LISTS = "The user is neither in the waiting list nor in the approved list.";


    private final TravelRepository travelRepository;

    /*Ilia*/

    public static final String UNAUTHORIZED_OPERATION = "Unauthorized operation.";
    public static final String OUT_OF_SEATS = "Out of seats";

    private final TravelStatusService travelStatusService;
    private final TravelRepository travelRepository;

    @Autowired
    public TravelServiceImpl(TravelStatusService travelStatusService, TravelRepository travelRepository) {
        this.travelStatusService = travelStatusService;
        this.travelRepository = travelRepository;
    }

    @Override
    public Travel createTravel(Travel travel, User creator) {
        isUserBlocked(creator, UNAUTHORIZED_OPERATION_BLOCKED);
        isUserVerified(creator, UNAUTHORIZED_OPERATION_NOT_VERIFIED);
        return travelRepository.createTravel(travel);
    }

    @Override
    public Travel cancelTravel(Travel travel) {
        return null;
    }

    public static void isUserBlocked(User userToBeChecked, String message) {
        if (userToBeChecked.isBlocked()) {
            throw new UnauthorizedOperationException(message);
        }
    }

    public static void isUserVerified(User userToBeChecked, String message) {
        if (userToBeChecked.isVerified()) {
            throw new UnauthorizedOperationException(message);
        }
    }

    /*TODO we should implement updateTravel method for cancel + complete*/
    @Override
    public Travel cancelTravel(Travel travel, User loggedUser) {
        if(!travel.getDriver().equals(loggedUser)){
            throw new UnauthorizedOperationException(UNAUTHORIZED_OPERATION);
        }
        TravelStatus cancelStatus = travelStatusService.getStatusById(2);
        travel.setStatus(cancelStatus);
        return travelRepository.updateTravel(travel);
    }

    @Override
    public Travel completeTravel(Travel travel) {
        return travelRepository.updateTravel(travel);
    }

    /*Ilia TODO we do not need logged user. The same will be for
     *       getTravelsByPassenger method.*/
    @Override
    public List<Travel> getTravelsByDriver(User driver, TravelFilterOptions travelFilterOptions) {
        return travelRepository.getTravelsByDriver(driver, travelFilterOptions);
    }

    @Override
    public List<Travel> getTravelsByPassenger(User passenger, User loggedUser) {
        if(!passenger.equals(loggedUser)){
            throw new UnauthorizedOperationException(UNAUTHORIZED_OPERATION);
        }
        return travelRepository.getTravelsByPassenger(passenger);
    }

    @Override
    public Travel getTravelById(int travelId) {
        return travelRepository.getTravelById(travelId);
    }

    /*Ilia*/
    @Override
    public List<Travel> getAllTravels(TravelFilterOptions travelFilterOptions) {
        return travelRepository.getAllTravels(travelFilterOptions);
    }

    @Override
    public Travel applyForATrip(User userToApply, Travel travelToApplyFor) {
        if (travelToApplyFor.getFreeSeats() < 1) {
            throw new InvalidOperationException(OUT_OF_SEATS);
        }
        if(travelToApplyFor.getUsersAppliedForTheTravel().contains(userToApply)){
            throw new InvalidOperationException(OUT_OF_SEATS);
        }
        travelToApplyFor.getUsersAppliedForTheTravel().add(userToApply);
        return travelRepository.updateTravel(travelToApplyFor);
    }

    /*TODO User who is logged in to be added. We have to check if the logged user is the same as the driver.*/
    @Override
    public Travel approvePassenger(User userToApprove, Travel travel) {
        Set<User> usersAppliedForTheTravel = travel.getUsersAppliedForTheTravel();
        usersAppliedForTheTravel.remove(userToApprove);
        Set<User> usersApprovedForTheTravel = travel.getUsersApprovedForTheTravel();
        usersApprovedForTheTravel.add(userToApprove);
        return travelRepository.updateTravel(travel);
    }

    /*Ilia*/
    @Override
    public Travel declinePassenger(User userToBeDeclined, Travel travel, User userLoggedIn) {
        isUserTheDriver(travel, userLoggedIn, UNAUTHORIZED_OPERATION_NOT_DRIVER);
        Set<User> usersAppliedForTheTravel = travel.getUsersAppliedForTheTravel();
        Set<User> usersApprovedForTheTravel = travel.getUsersApprovedForTheTravel();
        hasUserAppliedOrBeingApprovedForTheTravel(
                userToBeDeclined,
                usersAppliedForTheTravel,
                usersApprovedForTheTravel);
        usersAppliedForTheTravel.remove(userToBeDeclined);
        usersApprovedForTheTravel.remove(userToBeDeclined);
        travel.setUsersAppliedForTheTravel(usersAppliedForTheTravel);
        travel.setUsersApprovedForTheTravel(usersApprovedForTheTravel);
        return travelRepository.updateTravel(travel);
    }

    private void hasUserAppliedOrBeingApprovedForTheTravel(User userToBeDeclined,
                                                           Set<User> usersAppliedForTheTravel,
                                                           Set<User> usersApprovedForTheTravel) {
        if (!usersAppliedForTheTravel.contains(userToBeDeclined) ||
        !usersApprovedForTheTravel.contains(userToBeDeclined)) {
            throw new EntityNotFoundException(USER_NOT_IN_TRAVEL_LISTS);
        }
    }

    private void isUserTheDriver(Travel travel, User userToBeChecked, String message) {
        if (!travel.getDriver().equals(userToBeChecked)) {
            throw new UnauthorizedOperationException(message);
        }
    }

}
