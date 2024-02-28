package com.tripweaver.models;

import jakarta.persistence.*;
import org.springframework.context.annotation.EnableMBeanExport;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "travels")
@SecondaryTable(name = "travel_comments", pkJoinColumns = @PrimaryKeyJoinColumn(name = "travel_id"))
public class Travel implements Comparable<Travel> {

    @Id
    @Column(name = "travel_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int travelId;
    @Column(name = "starting_point")
    private String startingPoint;
    @ManyToOne(targetEntity=City.class,fetch = FetchType.EAGER)
    @JoinColumn(name = "starting_point_city_id")
    private City startingPointCity;
    @Column(name = "starting_point_address")
    private String startingPointAddress;
    @ManyToOne(targetEntity=City.class,fetch = FetchType.EAGER)
    @JoinColumn(name = "ending_point_city_id")
    private City endingPointCity;
    @Column(name = "ending_point_address")
    private String endingPointAddress;
    @Column(name = "ending_point")
    private String endingPoint;

    @Column(name = "departure_time")
    private LocalDateTime departureTime;

    @Column(name = "free_seats")
    private int freeSeats;
    @Column(name = "created")
    private LocalDateTime createdOn;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User driver;
    @Column(name = "travel_comment_content", table = "travel_comments")
    private String comment;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinTable(name = "travels_travel_statuses",
            joinColumns = @JoinColumn(name = "travel_id"),
            inverseJoinColumns = @JoinColumn(name = "travel_status_id"))
    private TravelStatus status;
    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "travels_users_applied",
            joinColumns = @JoinColumn(name = "travel_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> usersAppliedForTheTravel;
    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "travels_users_approved",
            joinColumns = @JoinColumn(name = "travel_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> usersApprovedForTheTravel;
    public Travel() {
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public TravelStatus getStatus() {
        return status;
    }

    public void setStatus(TravelStatus status) {
        this.status = status;
    }

    public Set<User> getUsersAppliedForTheTravel() {
        return usersAppliedForTheTravel;
    }

    public void setUsersAppliedForTheTravel(Set<User> usersAppliedForTheTravel) {
        this.usersAppliedForTheTravel = usersAppliedForTheTravel;
    }

    public Set<User> getUsersApprovedForTheTravel() {
        return usersApprovedForTheTravel;
    }

    public void setUsersApprovedForTheTravel(Set<User> usersApprovedForTheTravel) {
        this.usersApprovedForTheTravel = usersApprovedForTheTravel;
    }

    public int getTravelId() {
        return travelId;
    }

    public void setTravelId(int travelId) {
        this.travelId = travelId;
    }

    public String getStartingPoint() {
        return startingPoint;
    }

    public void setStartingPoint(String startingPoint) {
        this.startingPoint = startingPoint;
    }

    public String getEndingPoint() {
        return endingPoint;
    }

    public void setEndingPoint(String endingPoint) {
        this.endingPoint = endingPoint;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public int getFreeSeats() {
        return freeSeats;
    }

    public void setFreeSeats(int freeSeats) {
        this.freeSeats = freeSeats;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public User getDriver() {
        return driver;
    }

    public void setDriver(User driver) {
        this.driver = driver;
    }

    public City getStartingPointCity() {
        return startingPointCity;
    }

    public void setStartingPointCity(City startingPointCity) {
        this.startingPointCity = startingPointCity;
    }

    public String getStartingPointAddress() {
        return startingPointAddress;
    }

    public void setStartingPointAddress(String startingPointAddress) {
        this.startingPointAddress = startingPointAddress;
    }

    public City getEndingPointCity() {
        return endingPointCity;
    }

    public void setEndingPointCity(City endingPointCity) {
        this.endingPointCity = endingPointCity;
    }

    public String getEndingPointAddress() {
        return endingPointAddress;
    }

    public void setEndingPointAddress(String endingPointAddress) {
        this.endingPointAddress = endingPointAddress;
    }

    @Override
    public int compareTo(Travel o) {
        return Integer.compare(this.getTravelId(), o.getTravelId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Travel travel = (Travel) o;
        return travelId == travel.travelId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(travelId);
    }
}
