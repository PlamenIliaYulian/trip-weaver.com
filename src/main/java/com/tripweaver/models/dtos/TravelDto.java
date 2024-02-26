package com.tripweaver.models.dtos;

import com.tripweaver.models.TravelStatus;
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
}
