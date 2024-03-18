package com.tripweaver.repositories;

import com.tripweaver.exceptions.EntityNotFoundException;
import com.tripweaver.models.CarPicture;
import com.tripweaver.repositories.contracts.CarPictureRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.tripweaver.services.helpers.ConstantHelper.DEFAULT_CAR_PICTURE_ID;

@Repository
public class CarPictureRepositoryImpl extends UploadableImpl implements CarPictureRepository {

    private final SessionFactory sessionFactory;

    public CarPictureRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    @Override
    public CarPicture createCarPicture(CarPicture carPicture) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(carPicture);
            session.getTransaction().commit();
        }
        return getCarPictureById(carPicture.getCarPictureId());
    }


    @Override
    public CarPicture getDefaultCarPicture() {
        try (Session session = sessionFactory.openSession()) {
            Query<CarPicture> query = session.createQuery("FROM CarPicture WHERE carPictureId = :id", CarPicture.class);
            query.setParameter("id", DEFAULT_CAR_PICTURE_ID);
            List<CarPicture> result = query.list();
            if (result.isEmpty()) {
                throw new EntityNotFoundException("CarPicture", "carPicture ID", String.valueOf(DEFAULT_CAR_PICTURE_ID));
            }
            return result.get(0);
        }
    }

    @Override
    public CarPicture getCarPictureById(int id) {
        try (Session session = sessionFactory.openSession()) {
            Query<CarPicture> query = session.createQuery("from CarPicture where carPictureId = :id", CarPicture.class);
            query.setParameter("id", id);
            if (query.list().isEmpty()) {
                throw new EntityNotFoundException("CarPicture", id);
            }
            return query.list().get(0);
        }
    }

}
