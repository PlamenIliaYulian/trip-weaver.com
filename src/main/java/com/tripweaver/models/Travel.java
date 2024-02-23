package com.tripweaver.models;

import jakarta.persistence.*;
import org.springframework.context.annotation.EnableMBeanExport;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "travels")
@SecondaryTable(name = "travel_comments", pkJoinColumns = @PrimaryKeyJoinColumn(name = "travel_id"))
public class Travel implements Comparable<Travel> {

    @Id
    @Column(name = "travel_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int travelId;

    /*TODO to add approved and pending passengers ( USER.class)*/

    @Column(name = "starting_point")
    private String startingPoint;

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

    public Travel() {
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
