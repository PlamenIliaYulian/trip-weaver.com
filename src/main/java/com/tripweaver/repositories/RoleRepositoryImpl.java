package com.tripweaver.repositories;

import com.tripweaver.exceptions.EntityNotFoundException;
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
        try (Session session = sessionFactory.openSession()) {
            Query<Role> query = session.createQuery("FROM Role WHERE roleId = :id ", Role.class);
            query.setParameter("id", id);
            List<Role> result = query.list();
            if (result.isEmpty()) {
                throw new EntityNotFoundException("Role", id);
            }
            return result.get(0);
        }
    }

    @Override
    public Role getRoleByName(String name) {
        try(Session session = sessionFactory.openSession()) {
            Query<Role> query = session.createQuery("From Role WHERE roleName = :name ", Role.class);
            query.setParameter("roleName", name);
            List<Role> result = query.list();
            if(result.isEmpty()){
                throw new EntityNotFoundException("Role", "name", name);
            }
            return result.get(0);
        }
    }

    @Override
    public List<Role> getAllRoles() {
        try (Session session = sessionFactory.openSession()) {
            Query<Role> query = session.createQuery("FROM Role", Role.class);
            return query.list();
        }
    }
}
