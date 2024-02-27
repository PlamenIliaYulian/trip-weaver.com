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
import com.tripweaver.services.helpers.PermissionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import com.tripweaver.services.contracts.TravelStatusService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class TravelServiceImpl implements TravelService {

    public static final String UNAUTHORIZED_OPERATION_BLOCKED = "Unauthorized operation. User blocked.";
    public static final String UNAUTHORIZED_OPERATION_NOT_VERIFIED = "Unauthorized operation. User not verified.";
    public static final String UNAUTHORIZED_OPERATION_NOT_DRIVER = "Unauthorized operation. User not driver of the travel.";
    public static final String UNAUTHORIZED_OPERATION_ALREADY_APPLIED = "Unauthorized operation. User already in waiting list.";
    public static final String USER_NOT_IN_TRAVEL_LISTS = "The user is neither in the waiting list nor in the approved list.";
    public static final String UNAUTHORIZED_OPERATION = "Unauthorized operation.";
    public static final int TRAVEL_STATUS_CANCEL_ID = 2;
    public static final int TRAVEL_STATUS_COMPLETE_ID = 3;
    public static final String TRAVEL_NOT_AVAILABLE = "Travel not available";
    public static final String INVALID_OPERATION = "User has not applied for this travel";
    public static final String INVALID_DEPARTURE_TIME = "Departure time cannot be before current moment.";
    private final TravelRepository travelRepository;
    private final TravelStatusService travelStatusService;
    private final PermissionHelper permissionHelper;

    @Autowired
    public TravelServiceImpl(TravelStatusService travelStatusService, TravelRepository travelRepository, PermissionHelper permissionHelper) {
        this.travelStatusService = travelStatusService;
        this.travelRepository = travelRepository;
        this.permissionHelper = permissionHelper;
    }

    @Override
    public Travel createTravel(Travel travel, User creator) {
        permissionHelper.isUserBlocked(creator, UNAUTHORIZED_OPERATION_BLOCKED);
        permissionHelper.isUserVerified(creator, UNAUTHORIZED_OPERATION_NOT_VERIFIED);
        permissionHelper.isDepartureTimeBeforeCurrentMoment(travel, INVALID_DEPARTURE_TIME);
        return travelRepository.createTravel(travel);
    }


    @Override
    public Travel cancelTravel(Travel travel, User loggedUser) {
        permissionHelper.isUserTheDriver(travel, loggedUser, UNAUTHORIZED_OPERATION_NOT_DRIVER);
        TravelStatus cancelStatus = travelStatusService.getStatusById(TRAVEL_STATUS_CANCEL_ID);
        travel.setStatus(cancelStatus);
        return travelRepository.updateTravel(travel);
    }

    @Override
    public Travel completeTravel(Travel travel, User loggedUser) {
        permissionHelper.isUserTheDriver(travel, loggedUser, UNAUTHORIZED_OPERATION_NOT_DRIVER);
        TravelStatus completeStatus = travelStatusService.getStatusById(TRAVEL_STATUS_COMPLETE_ID);
        travel.setStatus(completeStatus);
        return travelRepository.updateTravel(travel);
    }

    /*Ilia*/
    @Override
    public List<Travel> getTravelsByDriver(User driver, User loggedUser, TravelFilterOptions travelFilterOptions) {
        permissionHelper.isSameUser(driver, loggedUser, UNAUTHORIZED_OPERATION);
        travelFilterOptions.setDriverId(Optional.of(driver.getUserId()));
        return travelRepository.getAllTravels(travelFilterOptions);
    }

    /*TODO Put TravelFilterOptions.*/
    @Override
    public List<Travel> getTravelsByPassenger(User passenger, User loggedUser, TravelFilterOptions travelFilterOptions) {
        permissionHelper.isSameUser(passenger, loggedUser, UNAUTHORIZED_OPERATION);
        travelFilterOptions.setPassengerId(Optional.of(passenger.getUserId()));
        return travelRepository.getAllTravels(travelFilterOptions);
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
        permissionHelper.isUserVerified(userToApply, UNAUTHORIZED_OPERATION_NOT_VERIFIED);
        permissionHelper.isUserBlocked(userToApply, UNAUTHORIZED_OPERATION_BLOCKED);
        permissionHelper.hasYetToApply(userToApply, travelToApplyFor, UNAUTHORIZED_OPERATION_ALREADY_APPLIED);
        permissionHelper.isTravelOpenForApplication(travelToApplyFor, TRAVEL_NOT_AVAILABLE);
        travelToApplyFor.getUsersAppliedForTheTravel().add(userToApply);
        return travelRepository.updateTravel(travelToApplyFor);
    }


    /*TODO User who is logged in to be added. We have to check if the logged user is the same as the driver.*/
    @Override
    public Travel approvePassenger(User userToBeApproved, User loggedUser, Travel travel) {
        permissionHelper.isUserBlocked(userToBeApproved, UNAUTHORIZED_OPERATION_BLOCKED);
        permissionHelper.isUserBlocked(loggedUser, UNAUTHORIZED_OPERATION_BLOCKED);
        permissionHelper.isUserTheDriver(travel, loggedUser, UNAUTHORIZED_OPERATION_NOT_DRIVER);
        Set<User> usersAppliedForTheTravel = travel.getUsersAppliedForTheTravel();
        permissionHelper.hasAlreadyApplied(userToBeApproved, travel, INVALID_OPERATION);
        usersAppliedForTheTravel.remove(userToBeApproved);
        Set<User> usersApprovedForTheTravel = travel.getUsersApprovedForTheTravel();
        usersApprovedForTheTravel.add(userToBeApproved);
        return travelRepository.updateTravel(travel);
    }

    /*Ilia*/
    @Override
    public Travel declinePassenger(User userToBeDeclined, Travel travel, User userLoggedIn) {
        permissionHelper.isUserTheDriver(travel, userLoggedIn, UNAUTHORIZED_OPERATION_NOT_DRIVER);
        Set<User> usersAppliedForTheTravel = travel.getUsersAppliedForTheTravel();
        Set<User> usersApprovedForTheTravel = travel.getUsersApprovedForTheTravel();
        permissionHelper.hasUserAppliedOrBeingApprovedForTheTravel(
                userToBeDeclined,
                usersAppliedForTheTravel,
                usersApprovedForTheTravel,
                USER_NOT_IN_TRAVEL_LISTS);
        usersAppliedForTheTravel.remove(userToBeDeclined);
        usersApprovedForTheTravel.remove(userToBeDeclined);
        return travelRepository.updateTravel(travel);
    }


}
