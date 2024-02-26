package com.tripweaver.models.filterOptions;

import java.util.Optional;

public class TravelFilterOptions {

    private Optional<String> startingPoint;
    private Optional<String> endingPoint;
    private Optional<String> departureBefore;
    private Optional<String> departureAfter;
    private Optional<Integer> minFreeSeats;
    private Optional<String> driverUsername;
    private Optional<String> commentContains;
    private Optional<Integer> statusId;
    private Optional<String> sortBy;
    private Optional<String> sortOrder;

    public TravelFilterOptions(String startingPoint,
                               String endingPoint,
                               String departureBefore,
                               String departureAfter,
                               int minFreeSeats,
                               String driverUsername,
                               String commentContains,
                               int statusId,
                               String sortBy,
                               String sortOrder) {
        this.startingPoint = Optional.ofNullable(startingPoint);
        this.endingPoint = Optional.ofNullable(endingPoint);
        this.departureBefore = Optional.ofNullable(departureBefore);
        this.departureAfter = Optional.ofNullable(departureAfter);
        this.minFreeSeats = Optional.of(minFreeSeats);
        this.driverUsername = Optional.ofNullable(driverUsername);
        this.commentContains = Optional.ofNullable(commentContains);
        this.statusId = Optional.of(statusId);
        this.sortBy = Optional.ofNullable(sortBy);
        this.sortOrder = Optional.ofNullable(sortOrder);
    }

    public Optional<String> getStartingPoint() {
        return startingPoint;
    }

    public Optional<String> getEndingPoint() {
        return endingPoint;
    }

    public Optional<String> getDepartureBefore() {
        return departureBefore;
    }

    public Optional<String> getDepartureAfter() {
        return departureAfter;
    }

    public Optional<Integer> getMinFreeSeats() {
        return minFreeSeats;
    }

    public Optional<String> getDriverUsername() {
        return driverUsername;
    }

    public Optional<String> getCommentContains() {
        return commentContains;
    }

    public Optional<Integer> getStatusId() {
        return statusId;
    }

    public Optional<String> getSortBy() {
        return sortBy;
    }

    public Optional<String> getSortOrder() {
        return sortOrder;
    }
}
