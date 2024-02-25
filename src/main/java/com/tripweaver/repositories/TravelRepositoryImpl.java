package com.tripweaver.repositories;

import com.tripweaver.exceptions.EntityNotFoundException;
import com.tripweaver.models.FeedbackForDriver;
import com.tripweaver.exceptions.EntityNotFoundException;
import com.tripweaver.models.Role;
import com.tripweaver.models.Travel;
import com.tripweaver.models.TravelFilterOptions;
import com.tripweaver.models.User;
import com.tripweaver.repositories.contracts.TravelRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
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

    /*TODO I believe we should remove this and complete methods from repo*/
    @Override
    public Travel cancelTravel(Travel travel) {
        return null;
    }

    @Override
    public Travel completeTravel(Travel travel) {
        return null;
    }

    /*TODO i believe we should have this methods for all updating methods in service*/
    @Override
    public Travel updateTravel(Travel travel) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(travel);
            session.getTransaction().commit();
        }
        return getTravelById(travel.getTravelId());
    }

    @Override
    public Travel getTravelById(int travelId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Travel> query = session.createQuery(
                    "FROM Travel WHERE travelId = :id", Travel.class);
            query.setParameter("id", travelId);
            if (query.list().isEmpty()) {
                throw new EntityNotFoundException("Travel", travelId);
            }
            return query.list().get(0);
        }
    }

    @Override
    public List<Travel> getTravelsByDriver(User driver) {
        return null;
    }

    /*TODo not sure if it's correct*/
    @Override
    public List<Travel> getTravelsByPassenger(User passenger) {
        try(Session session = sessionFactory.openSession()) {
            Query<Travel> query = session.createQuery("From Travel t JOIN t.usersApprovedForTheTravel u where u.userId= :userId ", Travel.class);
            query.setParameter("userId", passenger.getUserId());
            List<Travel> result = query.list();
            if(result.isEmpty()){
                throw new EntityNotFoundException("Travel", "userId", String.valueOf(passenger.getUserId()));
            }
            return result;
        }
    }

    @Override
    public List<Travel> getAllTravels(TravelFilterOptions travelFilterOptions) {
        return null;
    }


    /*TODo same here, we should remove this method from repo and use updateTravel instead*/
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
}
