package com.tripweaver.repositories;

import com.tripweaver.exceptions.EntityNotFoundException;
import com.tripweaver.models.Avatar;
import com.tripweaver.models.FeedbackForDriver;
import com.tripweaver.models.FeedbackForPassenger;
import com.tripweaver.repositories.contracts.FeedbackRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class FeedbackRepositoryImpl implements FeedbackRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public FeedbackRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public FeedbackForDriver getFeedbackForDriverById(int id) {
        try (Session session = sessionFactory.openSession()) {
            Query<FeedbackForDriver> query = session.createQuery(
                    "from FeedbackForDriver where feedbackId = :id", FeedbackForDriver.class);
            query.setParameter("id", id);
            if (query.list().isEmpty()) {
                throw new EntityNotFoundException("FeedbackForDriver", id);
            }
            return query.list().get(0);
        }
    }

    @Override
    public FeedbackForDriver createFeedbackForDriver(FeedbackForDriver feedbackForDriver) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(feedbackForDriver);
            session.getTransaction().commit();
            return getFeedbackForDriverById(feedbackForDriver.getFeedbackId());
        }
    }

    @Override
    public FeedbackForPassenger createFeedbackForPassenger(FeedbackForPassenger FeedbackForPassenger) {
        return null;
    }
}
