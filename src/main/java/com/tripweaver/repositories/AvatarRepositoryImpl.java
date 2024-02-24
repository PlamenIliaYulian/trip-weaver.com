package com.tripweaver.repositories;

import com.tripweaver.exceptions.EntityNotFoundException;
import com.tripweaver.models.Avatar;
import com.tripweaver.repositories.contracts.AvatarRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Repository
public class AvatarRepositoryImpl implements AvatarRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public AvatarRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    @Override
    public Avatar createAvatar(Avatar avatar) {
        return null;
    }


    @Override
    public Avatar getDefaultAvatar() {
        try (Session session = sessionFactory.openSession()) {
            Query<Avatar> query = session.createQuery("FROM Avatar WHERE avatarId = 1", Avatar.class);
            List<Avatar> result = query.list();
            if (result.isEmpty()) {
                throw new EntityNotFoundException("Avatar", "avatar ID", "1");
            }
            return result.get(0);
        }
    }

    @Override
    public String uploadPictureToCloudinary(MultipartFile multipartFile) {
        return null;
    }
}
