package com.tripweaver.services;

import com.tripweaver.models.Travel;
import com.tripweaver.models.TravelStatus;
import com.tripweaver.models.User;
import com.tripweaver.models.filterOptions.TravelFilterOptions;
import com.tripweaver.repositories.contracts.TravelRepository;
import com.tripweaver.services.contracts.BingMapService;
import com.tripweaver.services.contracts.TravelService;
import com.tripweaver.services.contracts.TravelStatusService;
import com.tripweaver.services.helpers.PermissionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
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
    public static final String INVALID_OPERATION_DRIVER = "User is the driver, so could not leaver Driver's feedback";
    public static final String INVALID_DEPARTURE_TIME = "Departure time cannot be before current moment.";
    public static final String KEY_COORDINATES = "coordinates";
    public static final String KEY_CITY = "city";
    public static final String KEY_TRAVEL_DISTANCE = "travelDistance";
    public static final String KEY_TRAVEL_DURATION = "travelDuration";
    public static final int TRAVEL_STATUS_CREATED_ID = 1;
    private final TravelRepository travelRepository;
    private final TravelStatusService travelStatusService;
    private final PermissionHelper permissionHelper;
    private final BingMapService bingMapService;

    @Autowired
    public TravelServiceImpl(TravelStatusService travelStatusService,
                             TravelRepository travelRepository,
                             PermissionHelper permissionHelper,
                             BingMapService bingMapService) {
        this.travelStatusService = travelStatusService;
        this.travelRepository = travelRepository;
        this.permissionHelper = permissionHelper;
        this.bingMapService = bingMapService;
    }

    @Override
    public Travel createTravel(Travel travel, User creatorDriver) {
        permissionHelper.isUserBlocked(creatorDriver, UNAUTHORIZED_OPERATION_BLOCKED);
        permissionHelper.isUserVerified(creatorDriver, UNAUTHORIZED_OPERATION_NOT_VERIFIED);
        permissionHelper.isDepartureTimeBeforeCurrentMoment(travel, INVALID_DEPARTURE_TIME);
        travel.setDriver(creatorDriver);
        travel.setCreatedOn(LocalDateTime.now());
        travel.setStatus(travelStatusService.getStatusById(TRAVEL_STATUS_CREATED_ID));

        StringBuilder startingPointAddress = new StringBuilder();
        startingPointAddress.append(travel.getStartingPointAddress()).append(",").append(travel.getStartingPointCity());
        HashMap<String, String> startingPointCoordinatesAndCity =
                bingMapService.getCoordinatesAndValidCityName(startingPointAddress.toString());
        StringBuilder endingPointAddress = new StringBuilder();
        endingPointAddress.append(travel.getEndingPointAddress()).append(",").append(travel.getEndingPointCity());
        HashMap<String, String> endingPointCoordinatesAndCity =
                bingMapService.getCoordinatesAndValidCityName(endingPointAddress.toString());

        String startingPointCoordinates = startingPointCoordinatesAndCity.get(KEY_COORDINATES);
        travel.setStartingPoint(startingPointCoordinates);
        travel.setStartingPointCity(startingPointCoordinatesAndCity.get(KEY_CITY));
        String endingPointCoordinates = endingPointCoordinatesAndCity.get(KEY_COORDINATES);
        travel.setEndingPoint(endingPointCoordinates);
        travel.setEndingPointCity(endingPointCoordinatesAndCity.get(KEY_CITY));

        HashMap<String, Integer> distanceAndDuration =
                bingMapService.calculateDistanceAndDuration(startingPointCoordinates, endingPointCoordinates);
        travel.setDistanceInKm(distanceAndDuration.get(KEY_TRAVEL_DISTANCE));
        int rideDurationInMinutes = distanceAndDuration.get(KEY_TRAVEL_DURATION);
        travel.setRideDurationInMinutes(rideDurationInMinutes);
        travel.setEstimatedArrivalTime(travel.getDepartureTime().plusMinutes(rideDurationInMinutes));

        return travelRepository.createTravel(travel);
    }

    @Override
    public Travel cancelTravel(Travel travel, User loggedUser) {
        permissionHelper.isUserTheDriver(travel, loggedUser, UNAUTHORIZED_OPERATION_NOT_DRIVER);
        permissionHelper.isTravelOpenForApplication(travel, TRAVEL_NOT_AVAILABLE);
        TravelStatus cancelStatus = travelStatusService.getStatusById(TRAVEL_STATUS_CANCEL_ID);
        travel.setStatus(cancelStatus);
        return travelRepository.updateTravel(travel);
    }

    @Override
    public Travel completeTravel(Travel travel, User loggedUser) {
        permissionHelper.isUserTheDriver(travel, loggedUser, UNAUTHORIZED_OPERATION_NOT_DRIVER);
        permissionHelper.isTravelOpenForApplication(travel, TRAVEL_NOT_AVAILABLE);
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
        permissionHelper.checkNotToBeDriver(travelToApplyFor, userToApply,INVALID_OPERATION_DRIVER);
        travelToApplyFor.getUsersAppliedForTheTravel().add(userToApply);
        return travelRepository.updateTravel(travelToApplyFor);
    }

    @Override
    public Travel approvePassenger(User userToBeApproved, User loggedUser, Travel travel) {
        permissionHelper.isUserBlocked(userToBeApproved, UNAUTHORIZED_OPERATION_BLOCKED);
        permissionHelper.isUserBlocked(loggedUser, UNAUTHORIZED_OPERATION_BLOCKED);
        permissionHelper.isUserTheDriver(travel, loggedUser, UNAUTHORIZED_OPERATION_NOT_DRIVER);
        permissionHelper.isTravelOpenForApplication(travel, TRAVEL_NOT_AVAILABLE);
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
        permissionHelper.isDriverOrSameUser(travel, userToBeDeclined, userLoggedIn, UNAUTHORIZED_OPERATION_NOT_DRIVER);
        permissionHelper.isTravelOpenForApplication(travel, TRAVEL_NOT_AVAILABLE);
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

    @Override
    public Long getAllTravelsCount() {
        return travelRepository.getAllTravelsCount();
    }


}
