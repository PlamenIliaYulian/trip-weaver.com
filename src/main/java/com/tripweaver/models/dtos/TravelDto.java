package com.tripweaver.models.dtos;

import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public class TravelDto {

    @NotEmpty(message = "Starting point city cannot be empty.")
    private String startingPointCity;
    @NotEmpty(message = "Starting point address cannot be empty.")
    private String startingPointAddress;
    @NotEmpty(message = "Ending point city cannot be empty.")
    private String endingPointCity;

    @NotEmpty(message = "Ending point address cannot be empty.")
    private String endingPointAddress;

    @NotNull(message = "Please, select a departure date.")
    @Future(message = "Must be a future date.")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDateTime departureTime;

    @Min(value = 1, message = "Free seats cannot be empty.")
    @Max(value = 50, message = "Free seats cannot be empty.")
    /*@Size(min = 1, max = 50, message = "Free seats must be between 1 and 50")*/
    private int freeSeats;

    @Size(max = 100, message = "The comment cannot be more than 100 symbols long.")
    private String comment;

    public TravelDto() {
    }

    public String getStartingPointCity() {
        return startingPointCity;
    }

    public void setStartingPointCity(String startingPointCity) {
        this.startingPointCity = startingPointCity;
    }

    public String getStartingPointAddress() {
        return startingPointAddress;
    }

    public void setStartingPointAddress(String startingPointAddress) {
        this.startingPointAddress = startingPointAddress;
    }

    public String getEndingPointCity() {
        return endingPointCity;
    }

    public void setEndingPointCity(String endingPointCity) {
        this.endingPointCity = endingPointCity;
    }

    public String getEndingPointAddress() {
        return endingPointAddress;
    }

    public void setEndingPointAddress(String endingPointAddress) {
        this.endingPointAddress = endingPointAddress;
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
