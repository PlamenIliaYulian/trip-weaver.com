package com.tripweaver.repositories;

import com.tripweaver.exceptions.EntityNotFoundException;
import com.tripweaver.models.Travel;
import com.tripweaver.models.filterOptions.TravelFilterOptions;
import com.tripweaver.repositories.contracts.TravelRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class TravelRepositoryImpl implements TravelRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public TravelRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Travel createTravel(Travel travel) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(travel);
            session.getTransaction().commit();
        }
        return travel;
    }

    @Override
    public Travel updateTravel(Travel travel) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(travel);
            session.getTransaction().commit();
        }
        return getTravelById(travel.getTravelId());
    }

    @Override
    public Travel getTravelById(int travelId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Travel> query = session.createQuery("FROM Travel WHERE travelId = :id", Travel.class);
            query.setParameter("id", travelId);
            if (query.list().isEmpty()) {
                throw new EntityNotFoundException("Travel", travelId);
            }
            return query.list().get(0);
        }
    }


    /*Ilia*/
    @Override
    public List<Travel> getAllTravels(TravelFilterOptions travelFilterOptions) {
        try (Session session = sessionFactory.openSession();) {
            List<String> filters = new ArrayList<>();
            Map<String, Object> parameters = new HashMap<>();

            travelFilterOptions.getStartingPointCity().ifPresent(value -> {
                filters.add(" travel.startingPointCity like :startingPointCity ");
                parameters.put("startingPointCity", String.format("%%%s%%", value));
            });
            travelFilterOptions.getEndingPointCity().ifPresent(value -> {
                filters.add(" travel.endingPointCity like :endingPointCity ");
                parameters.put("endingPointCity", String.format("%%%s%%", value));
            });
            travelFilterOptions.getDepartureBefore().ifPresent(value -> {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime date = LocalDateTime.parse(value, formatter);
                filters.add(" travel.departureTime <= :departureBefore ");
                parameters.put("departureBefore", date);
            });
            travelFilterOptions.getDepartureAfter().ifPresent(value -> {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime date = LocalDateTime.parse(value, formatter);
                filters.add(" travel.departureTime >= :departureAfter ");
                parameters.put("departureAfter", date);
            });
            travelFilterOptions.getMinFreeSeats().ifPresent(value -> {
                filters.add(" travel.freeSeats >= :minFreeSeats ");
                parameters.put("minFreeSeats", value);
            });
            travelFilterOptions.getDriverUsername().ifPresent(value -> {
                filters.add(" travel.driver.username like :driverUsername ");
                parameters.put("driverUsername", String.format("%%%s%%", value));
            });
            travelFilterOptions.getCommentContains().ifPresent(value -> {
                if (!value.isEmpty()) {
                    filters.add(" travel.comment like :commentContains ");
                    parameters.put("commentContains", String.format("%%%s%%", value));
                }
            });
            travelFilterOptions.getStatusId().ifPresent(value -> {
                filters.add(" travel.status.travelStatusId = :statusId ");
                parameters.put("statusId", value);
            });

            if (travelFilterOptions.getDriverId() != null && travelFilterOptions.getDriverId().get() != 0) {
                travelFilterOptions.getDriverId().ifPresent(value -> {
                    filters.add(" travel.driver.userId = :userId ");
                    parameters.put("userId", value);
                });
            }

            StringBuilder queryString;
            if (travelFilterOptions.getPassengerId() != null && travelFilterOptions.getPassengerId().get() != 0) {
                travelFilterOptions.getPassengerId().ifPresent(value -> {
                    filters.add(" passengers.userId = :userId ");
                    parameters.put("userId", value);
                });

                queryString = new StringBuilder("FROM Travel AS travel JOIN travel.usersApprovedForTheTravel passengers ");
                queryString.append(" WHERE ")
                        .append(String.join(" AND ", filters));
            } else {
                queryString = new StringBuilder("FROM Travel AS travel ");
                queryString.append(" WHERE ")
                        .append(String.join(" AND ", filters));
            }


            queryString.append(generateOrderBy(travelFilterOptions));
            Query<Travel> query = session.createQuery(queryString.toString(), Travel.class);
            query.setProperties(parameters);
            return query.list();
        }
    }

    @Override
    public Long getAllTravelsCount() {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery("select count(*) from Travel travel " +
                    "JOIN travel.status status where status.statusName = :name ", Long.class);
            query.setParameter("name", "COMPLETED");
            return query.list().get(0);
        }
    }

    private String generateOrderBy(TravelFilterOptions filterOptions) {
        if (filterOptions.getSortBy().isEmpty()) {
            return "";
        }
        String orderBy = "";
        switch (filterOptions.getSortBy().get()) {
            case "startingPointCity":
                orderBy = "startingPointCity";
                break;
            case "endingPointCity":
                orderBy = "endingPointCity";
                break;
            case "departureTime":
                orderBy = "departureTime";
                break;
            case "freeSeats":
                orderBy = "freeSeats";
                break;
            case "createdOn":
                orderBy = "createdOn";
                break;
            case "driver":
                orderBy = "driver.username";
                break;
            case "distance":
                orderBy = "distanceInKm";
                break;
            case "duration":
                orderBy = "rideDurationInMinutes";
                break;
            default:
                return "";
        }

        orderBy = String.format(" order by %s", orderBy);
        if (filterOptions.getSortOrder().isPresent() &&
                filterOptions.getSortOrder().get().equalsIgnoreCase("desc")) {
            orderBy = String.format("%s desc", orderBy);
        }

        return orderBy;
    }
}
