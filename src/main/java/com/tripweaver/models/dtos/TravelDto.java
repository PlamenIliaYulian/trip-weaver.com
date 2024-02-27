package com.tripweaver.models.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class TravelDto {

    @NotNull(message = "Starting point cannot be empty.")
    private String startingPoint;
    @NotNull(message = "Ending point cannot be empty.")
    private String endingPoint;

    @NotNull(message = "Departure time cannot be empty.")
    private LocalDateTime departureTime;

    @NotNull(message = "Free seats cannot be empty.")
    @Size(min = 1, max = 50, message = "Free seats must be between 1 and 50")
    private int freeSeats;

    @Size(max = 100, message = "The comment cannot be more than 100 symbols long.")
    private String comment;

    public TravelDto() {
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
