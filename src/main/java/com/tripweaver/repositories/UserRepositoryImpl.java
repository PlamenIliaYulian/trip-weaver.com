package com.tripweaver.repositories;

import com.tripweaver.exceptions.EntityNotFoundException;
import com.tripweaver.models.User;
import com.tripweaver.models.UserFilterOptions;
import com.tripweaver.repositories.contracts.UserRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final SessionFactory sessionFactory;

    public UserRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    @Override
    public User createUser(User user) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(user);
            session.getTransaction().commit();
        }
        return user;
    }

    @Override
    public User updateUser(User user) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(user);
            session.getTransaction().commit();
        }
        return user;
    }

    /*TODO check if getAllUsers is implemented correctly as we used it just for testing purposes*/
    @Override
    public List<User> getAllUsers(UserFilterOptions userFilterOptions) {
        try (Session session = sessionFactory.openSession()) {
            List<String> filters = new ArrayList<>();
            Map<String, Object> params = new HashMap<>();

            userFilterOptions.getUsername().ifPresent(value -> {
                filters.add("username like :username ");
                params.put("username", String.format("%%%s%%", value));
            });

            userFilterOptions.getEmail().ifPresent(value -> {
                filters.add("email like :email ");
                params.put("email", String.format("%%%s%%", value));
            });

            userFilterOptions.getFirstName().ifPresent(value -> {
                filters.add("firstName like :firstName ");
                params.put("firstName", String.format("%%%s%%", value));
            });


            filters.add(" isDeleted = false ");
            params.put("isDeleted", false);

            StringBuilder queryString = new StringBuilder("FROM User ");
            queryString.append(" WHERE ")
                    .append(String.join(" AND ", filters));


            queryString.append(generateOrderBy(userFilterOptions));
            Query<User> query = session.createQuery(queryString.toString(), User.class);
            query.setProperties(params);
            return query.list();
        }
    }

    @Override
    public User getUserByUsername(String username) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("FROM User WHERE username = :username AND isDeleted = false ", User.class);
            query.setParameter("username", username);
            List<User> result = query.list();
            if (result.isEmpty()) {
                throw new EntityNotFoundException("User", "username", username);
            }
            return result.get(0);
        }
    }

    @Override
    public User getUserByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("from User where email = :email and isDeleted = false ", User.class);
            query.setParameter("email", email);
            List<User> result = query.list();
            if (result.isEmpty()) {
                throw new EntityNotFoundException("User", "email", email);
            }
            return result.get(0);
        }
    }

    @Override
    public User getUserById(int id) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("FROM User WHERE userId = :id AND isDeleted = false ", User.class);
            query.setParameter("id", id);
            List<User> result = query.list();
            if (result.isEmpty()) {
                throw new EntityNotFoundException("User", id);
            }
            return result.get(0);
        }
    }

    @Override
    public User blockUser(User userToBeBlocked) {
        return null;
    }

    @Override
    public User unBlockUser(User userToBeUnBlocked) {
        return null;
    }

    @Override
    public User getUserByPhoneNumber(String phoneNumber) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("FROM User WHERE phoneNumber = :phoneNumber AND isDeleted = false ", User.class);
            query.setParameter("phoneNumber", phoneNumber);
            List<User> result = query.list();
            if (result.isEmpty()) {
                throw new EntityNotFoundException("User", "phoneNumber", phoneNumber);
            }
            return result.get(0);
        }
    }

    @Override
    public long getAllUsersCount() {
        return 0;
    }

    @Override
    public List<User> getTopTenTravelOrganizersByRating() {
        return null;
    }

    @Override
    public List<User> getTopTenTravelPassengersByRating() {
        return null;
    }

    private String generateOrderBy(UserFilterOptions userFilterOptions) {
        if (userFilterOptions.getSortBy().isEmpty()) {
            return "";
        }

        String orderBy = "";
        switch (userFilterOptions.getSortBy().get()) {
            case "username":
                orderBy = "username";
                break;
            case "email":
                orderBy = "user_email";
                break;
            case "firstName":
                orderBy = "first_name";
                break;
            default:
                return "";
        }

        orderBy = String.format(" order by %s", orderBy);

        if (userFilterOptions.getSortOrder().isPresent() && userFilterOptions.getSortOrder().get().equalsIgnoreCase("desc")) {
            orderBy = String.format("%s desc", orderBy);
        }

        return orderBy;
    }
}
