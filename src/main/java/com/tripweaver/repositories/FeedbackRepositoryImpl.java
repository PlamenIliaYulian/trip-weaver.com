package com.tripweaver.repositories;

import com.tripweaver.exceptions.EntityNotFoundException;
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

    @Override
    public FeedbackForDriver createFeedbackForDriver(FeedbackForDriver feedbackForDriver) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(feedbackForDriver);
            session.getTransaction().commit();
        }
        return feedbackForDriver;
    }

    @Override
    public FeedbackForPassenger createFeedbackForPassenger(FeedbackForPassenger FeedbackForPassenger) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(FeedbackForPassenger);
            session.getTransaction().commit();
        }
        return FeedbackForPassenger;
    }

    /*Ilia*/
    @Override
    public FeedbackForDriver getFeedbackForDriverById(int id) {
        try (Session session = sessionFactory.openSession()) {
            Query<FeedbackForDriver> query =
                    session.createQuery("from FeedbackForDriver where feedbackId = :id", FeedbackForDriver.class);
            query.setParameter("id", id);
            if (query.list().isEmpty()) {
                throw new EntityNotFoundException("Feedback for driver", id);
            }
            return query.list().get(0);
        }
    }

    /*Ilia*/
    @Override
    public FeedbackForPassenger getFeedbackForPassengerById(int id) {
        try (Session session = sessionFactory.openSession()) {
            Query<FeedbackForPassenger> query =
                    session.createQuery("from FeedbackForPassenger where feedbackId = :id", FeedbackForPassenger.class);
            query.setParameter("id", id);
            if (query.list().isEmpty()) {
                throw new EntityNotFoundException("Feedback for passenger", id);
            }
            return query.list().get(0);
        }
    }
}