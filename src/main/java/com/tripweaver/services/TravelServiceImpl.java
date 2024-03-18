package com.tripweaver.services;

import com.tripweaver.models.Travel;
import com.tripweaver.models.TravelStatus;
import com.tripweaver.models.User;
import com.tripweaver.models.filterOptions.TravelFilterOptions;
import com.tripweaver.repositories.contracts.TravelRepository;
import com.tripweaver.services.contracts.BingMapService;
import com.tripweaver.services.contracts.TravelService;
import com.tripweaver.services.contracts.TravelStatusService;
import com.tripweaver.services.helpers.ValidationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.tripweaver.services.helpers.ConstantHelper.*;

@Service
public class TravelServiceImpl implements TravelService {

    private final TravelRepository travelRepository;
    private final TravelStatusService travelStatusService;
    private final BingMapService bingMapService;

    @Autowired
    public TravelServiceImpl(TravelStatusService travelStatusService,
                             TravelRepository travelRepository,
                             BingMapService bingMapService) {
        this.travelStatusService = travelStatusService;
        this.travelRepository = travelRepository;
        this.bingMapService = bingMapService;
    }

    @Override
    public Travel createTravel(Travel travel, User creatorDriver) {
        ValidationHelper.isUserBlocked(creatorDriver, UNAUTHORIZED_OPERATION_BLOCKED);
        ValidationHelper.isUserVerified(creatorDriver, UNAUTHORIZED_OPERATION_NOT_VERIFIED);
        ValidationHelper.isDepartureTimeBeforeCurrentMoment(travel, INVALID_DEPARTURE_TIME);
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
        ValidationHelper.isUserTheDriver(travel, loggedUser, UNAUTHORIZED_OPERATION_NOT_DRIVER);
        ValidationHelper.isTravelOpenForApplication(travel, TRAVEL_NOT_AVAILABLE);
        TravelStatus cancelStatus = travelStatusService.getStatusById(TRAVEL_STATUS_CANCEL_ID);
        travel.setStatus(cancelStatus);
        return travelRepository.updateTravel(travel);
    }

    @Override
    public Travel completeTravel(Travel travel, User loggedUser) {
        ValidationHelper.isUserTheDriver(travel, loggedUser, UNAUTHORIZED_OPERATION_NOT_DRIVER);
        ValidationHelper.isTravelOpenForApplication(travel, TRAVEL_NOT_AVAILABLE);
        TravelStatus completeStatus = travelStatusService.getStatusById(TRAVEL_STATUS_COMPLETED);
        travel.setStatus(completeStatus);
        return travelRepository.updateTravel(travel);
    }

    @Override
    public List<Travel> getTravelsByDriver(User driver, User loggedUser, TravelFilterOptions travelFilterOptions) {
        ValidationHelper.isSameUser(driver, loggedUser, UNAUTHORIZED_OPERATION);
        travelFilterOptions.setDriverId(Optional.of(driver.getUserId()));
        return travelRepository.getAllTravels(travelFilterOptions);
    }

    @Override
    public List<Travel> getTravelsByPassenger(User passenger, User loggedUser, TravelFilterOptions travelFilterOptions) {
        ValidationHelper.isSameUser(passenger, loggedUser, UNAUTHORIZED_OPERATION);
        travelFilterOptions.setPassengerId(Optional.of(passenger.getUserId()));
        return travelRepository.getAllTravels(travelFilterOptions);
    }

    @Override
    public Travel getTravelById(int travelId) {
        return travelRepository.getTravelById(travelId);
    }

    @Override
    public List<Travel> getTravelsAsAppliedPassenger(User loggedUser, User passengerAppliedToTravels) {
        ValidationHelper.isSameUser(passengerAppliedToTravels, loggedUser, UNAUTHORIZED_OPERATION);
        return travelRepository.getTravelsAsAppliedPassenger(passengerAppliedToTravels);
    }

    @Override
    public List<Travel> getAllTravels(TravelFilterOptions travelFilterOptions) {
        return travelRepository.getAllTravels(travelFilterOptions);
    }

    @Override
    public Travel applyForATrip(User userToApply, Travel travelToApplyFor) {
        ValidationHelper.isUserVerified(userToApply, UNAUTHORIZED_OPERATION_NOT_VERIFIED);
        ValidationHelper.isUserBlocked(userToApply, UNAUTHORIZED_OPERATION_BLOCKED);
        ValidationHelper.hasYetToApply(userToApply, travelToApplyFor, UNAUTHORIZED_OPERATION_ALREADY_APPLIED);
        ValidationHelper.isTravelOpenForApplication(travelToApplyFor, TRAVEL_NOT_AVAILABLE);
        ValidationHelper.checkNotToBeDriver(travelToApplyFor, userToApply, INVALID_OPERATION_DRIVER);
        travelToApplyFor.getUsersAppliedForTheTravel().add(userToApply);
        return travelRepository.updateTravel(travelToApplyFor);
    }

    @Override
    public Travel approvePassenger(User userToBeApproved, User loggedUser, Travel travel) {
        ValidationHelper.isUserBlocked(userToBeApproved, UNAUTHORIZED_OPERATION_BLOCKED);
        ValidationHelper.isUserBlocked(loggedUser, UNAUTHORIZED_OPERATION_BLOCKED);
        ValidationHelper.isUserTheDriver(travel, loggedUser, UNAUTHORIZED_OPERATION_NOT_DRIVER);
        ValidationHelper.isTravelOpenForApplication(travel, TRAVEL_NOT_AVAILABLE);
        Set<User> usersAppliedForTheTravel = travel.getUsersAppliedForTheTravel();

        ValidationHelper.hasAlreadyApplied(userToBeApproved, travel, INVALID_OPERATION);
        Set<User> usersApprovedForTheTravel = travel.getUsersApprovedForTheTravel();
        ValidationHelper.isThereAnyFreeSeatsInTravel(travel, usersApprovedForTheTravel);
        usersAppliedForTheTravel.remove(userToBeApproved);
        usersApprovedForTheTravel.add(userToBeApproved);
        return travelRepository.updateTravel(travel);
    }

    @Override
    public Travel declinePassenger(User userToBeDeclined, Travel travel, User userLoggedIn) {
        ValidationHelper.isDriverOrSameUser(travel, userToBeDeclined, userLoggedIn, UNAUTHORIZED_OPERATION_NOT_DRIVER);
        ValidationHelper.isTravelOpenForApplication(travel, TRAVEL_NOT_AVAILABLE);
        Set<User> usersAppliedForTheTravel = travel.getUsersAppliedForTheTravel();
        Set<User> usersApprovedForTheTravel = travel.getUsersApprovedForTheTravel();
        ValidationHelper.hasUserAppliedOrBeingApprovedForTheTravel(
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
