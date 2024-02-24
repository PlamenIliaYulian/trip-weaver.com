package com.tripweaver.repositories;

import com.tripweaver.models.Role;
import com.tripweaver.repositories.contracts.RoleRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RoleRepositoryImpl implements RoleRepository {
    private final SessionFactory sessionFactory;

    @Autowired
    public RoleRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Role getRoleById(int id) {
        return null;
    }

    @Override
    public Role getRoleByName(String name) {
        return null;
    }

    @Override
    public List<Role> getAllRoles() {
        try (Session session = sessionFactory.openSession()) {
            Query<Role> query = session.createQuery("FROM Role", Role.class);
            return query.list();
        }
    }
}
