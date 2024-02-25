package com.tripweaver.services;

import com.tripweaver.exceptions.InvalidOperationException;
import com.tripweaver.exceptions.UnauthorizedOperationException;
import com.tripweaver.models.Travel;
import com.tripweaver.models.TravelFilterOptions;
import com.tripweaver.models.TravelStatus;
import com.tripweaver.models.User;
import com.tripweaver.repositories.contracts.TravelRepository;
import com.tripweaver.services.contracts.TravelService;
import org.springframework.beans.factory.annotation.Autowired;
import com.tripweaver.services.contracts.TravelStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class TravelServiceImpl implements TravelService {

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
    public Travel createTravel(Travel travel) {
        return null;
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

    @Override
    public List<Travel> getTravelsByDriver(User driver, User loggedUser) {
        return null;
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

    @Override
    public List<Travel> getAllTravels(TravelFilterOptions travelFilterOptions) {
        return null;
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

    @Override
    public Travel approvePassenger(User userToApprove, Travel travel) {
        Set<User> usersAppliedForTheTravel = travel.getUsersAppliedForTheTravel();
        usersAppliedForTheTravel.remove(userToApprove);
        Set<User> usersApprovedForTheTravel = travel.getUsersApprovedForTheTravel();
        usersApprovedForTheTravel.add(userToApprove);
        return travelRepository.updateTravel(travel);
    }

    @Override
    public Travel declinePassenger(User userToBeDeclined, Travel travel) {
        return null;
    }
}
