package com.tripweaver.services;

import com.tripweaver.exceptions.EntityNotFoundException;
import com.tripweaver.exceptions.UnauthorizedOperationException;
import com.tripweaver.exceptions.InvalidOperationException;
import com.tripweaver.models.Travel;
import com.tripweaver.models.filterOptions.TravelFilterOptions;
import com.tripweaver.models.TravelStatus;
import com.tripweaver.models.User;
import com.tripweaver.repositories.contracts.TravelRepository;
import com.tripweaver.services.contracts.TravelService;
import org.springframework.beans.factory.annotation.Autowired;
import com.tripweaver.services.contracts.TravelStatusService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class TravelServiceImpl implements TravelService {

    public static final String UNAUTHORIZED_OPERATION_BLOCKED = "Unauthorized operation. User blocked.";
    public static final String UNAUTHORIZED_OPERATION_NOT_VERIFIED = "Unauthorized operation. User not verified.";
    public static final String UNAUTHORIZED_OPERATION_NOT_DRIVER = "Unauthorized operation. User not driver of the travel.";
    public static final String UNAUTHORIZED_OPERATION_ALREADY_APPLIED = "Unauthorized operation. User already in waiting list.";
    public static final String USER_NOT_IN_TRAVEL_LISTS = "The user is neither in the waiting list nor in the approved list.";
    public static final String UNAUTHORIZED_OPERATION = "Unauthorized operation.";
    public static final int TRAVEL_STATUS_CREATED_ID = 1;
    public static final int TRAVEL_STATUS_CANCEL_ID = 2;
    public static final int TRAVEL_STATUS_COMPLETE_ID = 3;
    public static final String TRAVEL_NOT_AVAILABLE = "Travel not available";
    private final TravelRepository travelRepository;
    private final TravelStatusService travelStatusService;

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


    @Override
    public Travel cancelTravel(Travel travel, User loggedUser) {
        if(!travel.getDriver().equals(loggedUser)){
            throw new UnauthorizedOperationException(UNAUTHORIZED_OPERATION_NOT_DRIVER);
        }
        TravelStatus cancelStatus = travelStatusService.getStatusById(TRAVEL_STATUS_CANCEL_ID);
        travel.setStatus(cancelStatus);
        return travelRepository.updateTravel(travel);
    }

    @Override
    public Travel completeTravel(Travel travel, User loggedUser) {
        if(!travel.getDriver().equals(loggedUser)){
            throw new UnauthorizedOperationException(UNAUTHORIZED_OPERATION_NOT_DRIVER);
        }
        TravelStatus completeStatus = travelStatusService.getStatusById(TRAVEL_STATUS_COMPLETE_ID);
        travel.setStatus(completeStatus);
        return travelRepository.updateTravel(travel);
    }

    /*Ilia TODO return logged user to check if you are the same user.*/
    @Override
    public List<Travel> getTravelsByDriver(User driver, User loggedUser, TravelFilterOptions travelFilterOptions) {
        if(!driver.equals(loggedUser)){
            throw new UnauthorizedOperationException(UNAUTHORIZED_OPERATION);
        }
        return travelRepository.getTravelsByDriver(driver, travelFilterOptions);
    }

    /*TODO Put TravelFilterOptions.*/
    @Override
    public List<Travel> getTravelsByPassenger(User passenger, User loggedUser, TravelFilterOptions travelFilterOptions) {
        if(!passenger.equals(loggedUser)){
            throw new UnauthorizedOperationException(UNAUTHORIZED_OPERATION);
        }
        return travelRepository.getTravelsByPassenger(passenger,travelFilterOptions);
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
        if(travelToApplyFor.getUsersAppliedForTheTravel().contains(userToApply)){
            throw new InvalidOperationException(UNAUTHORIZED_OPERATION_ALREADY_APPLIED);
        }
        if(!travelToApplyFor.getStatus().equals(travelStatusService.getStatusById(TRAVEL_STATUS_CREATED_ID))){
            throw new UnauthorizedOperationException(TRAVEL_NOT_AVAILABLE);
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
