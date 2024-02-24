package com.tripweaver.repositories;

import com.tripweaver.models.Travel;
import com.tripweaver.models.TravelFilterOptions;
import com.tripweaver.models.User;
import com.tripweaver.repositories.contracts.TravelRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class TravelRepositoryImpl implements TravelRepository {
    private final SessionFactory sessionFactory;

    @Autowired
    public TravelRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    @Override
    public Travel createTravel(Travel travel) {
        return null;
    }

    @Override
    public Travel cancelTravel(Travel travel) {
        return null;
    }

    @Override
    public Travel completeTravel(Travel travel) {
        return null;
    }

    @Override
    public Travel getTravelById(int travelId) {
        return null;
    }

    @Override
    public List<Travel> getTravelsByDriver(User driver) {
        return null;
    }

    @Override
    public List<Travel> getTravelsByPassenger(User passenger) {
        return null;
    }

    @Override
    public List<Travel> getAllTravels(TravelFilterOptions travelFilterOptions) {
        return null;
    }

    @Override
    public Travel applyForATrip(User userToApply, Travel travelToApplyFor) {
        return null;
    }

    @Override
    public Travel approvePassenger(User userToApprove, Travel travel) {
        return null;
    }

    @Override
    public Travel declinePassenger(User userToBeDeclined, Travel travel) {
        return null;
    }

    @Override
    public Travel updateTravel(Travel travel) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(travel);
            session.getTransaction().commit();
        }
        return travel;
    }
}
