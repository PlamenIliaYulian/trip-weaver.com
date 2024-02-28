package com.tripweaver.repositories;

import com.tripweaver.exceptions.EntityNotFoundException;
import com.tripweaver.models.Feedback;
import com.tripweaver.models.enums.FeedbackType;
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


    /*Ilia*/
    @Override
    public Feedback getFeedbackForDriverById(int id) {
        try (Session session = sessionFactory.openSession()) {
            Query<Feedback> query =
                    session.createQuery("from Feedback where feedbackId = :id AND feedbackType = :type", Feedback.class);
            query.setParameter("id", id);
            query.setParameter("type", FeedbackType.FOR_DRIVER);
            if (query.list().isEmpty()) {
                throw new EntityNotFoundException("Feedback for driver", id);
            }
            return query.list().get(0);
        }
    }

    /*Ilia*/
    @Override
    public Feedback getFeedbackForPassengerById(int id) {
        try (Session session = sessionFactory.openSession()) {
            Query<Feedback> query =
                    session.createQuery("from Feedback where feedbackId = :id AND feedbackType = :type", Feedback.class);
            query.setParameter("id", id);
            query.setParameter("type", FeedbackType.FOR_PASSENGER);
            if (query.list().isEmpty()) {
                throw new EntityNotFoundException("Feedback for driver", id);
            }
            return query.list().get(0);
        }
    }

    @Override
    public Feedback createFeedback(Feedback feedback) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(feedback);
            session.getTransaction().commit();
        }
        return feedback;
    }
}