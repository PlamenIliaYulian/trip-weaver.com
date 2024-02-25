package com.tripweaver.repositories;

import com.tripweaver.exceptions.EntityNotFoundException;
import com.tripweaver.models.TravelStatus;
import com.tripweaver.repositories.contracts.TravelStatusRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class TravelStatusRepositoryImpl implements TravelStatusRepository {

    private final SessionFactory sessionFactory;

    public TravelStatusRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public TravelStatus getStatusById(int travelStatusId) {
        try (Session session = sessionFactory.openSession()) {
            Query<TravelStatus> query = session.createQuery("FROM TravelStatus WHERE travelStatusId = :travelStatusId ", TravelStatus.class);
            query.setParameter("travelStatusId", travelStatusId);
            List<TravelStatus> result = query.list();
            if (result.isEmpty()) {
                throw new EntityNotFoundException("TravelStatus", travelStatusId);
            }
            return result.get(0);
        }
    }
}
