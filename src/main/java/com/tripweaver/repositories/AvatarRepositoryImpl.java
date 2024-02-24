package com.tripweaver.repositories;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.tripweaver.exceptions.EntityNotFoundException;
import com.tripweaver.exceptions.EntityNotFoundException;
import com.tripweaver.models.Avatar;
import com.tripweaver.repositories.contracts.AvatarRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import java.io.IOException;
import java.util.Map;

@Repository
public class AvatarRepositoryImpl implements AvatarRepository {

    private final String CLOUDINARY_URL = "cloudinary://242857587276945:B5ODyO381gN-4aFLKDNVcrAFzxM@dol3hflxs";

    private final SessionFactory sessionFactory;

    public AvatarRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Avatar createAvatar(Avatar avatar) {
        try (Session session = sessionFactory.openSession()){
            session.beginTransaction();
            session.persist(avatar);
            session.getTransaction().commit();
            return getAvatarById(avatar.getAvatarId());
        }
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

    /*Ilia*/
    @Override
    public String uploadPictureToCloudinary(MultipartFile multipartFile) {
        Cloudinary cloudinary = new Cloudinary(CLOUDINARY_URL);
        cloudinary.config.secure = true;
        try {
            Map params1 = ObjectUtils.asMap(
                    "use_filename", true,
                    "unique_filename", false,
                    "overwrite", true
            );

            return cloudinary
                    .uploader()
                    .upload(multipartFile.getBytes(), params1)
                    .get("secure_url")
                    .toString();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Avatar getAvatarById(int id) {
        try (Session session = sessionFactory.openSession()) {
            Query<Avatar> query = session.createQuery("from Avatar where avatarId = :id", Avatar.class);
            query.setParameter("id", id);
            if (query.list().isEmpty()) {
                throw new EntityNotFoundException("Avatar", id);
            }
            return query.list().get(0);
        }
    }
}
