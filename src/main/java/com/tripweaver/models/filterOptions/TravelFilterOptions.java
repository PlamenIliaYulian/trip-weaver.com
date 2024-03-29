package com.tripweaver.models.filterOptions;

import java.util.Optional;

public class TravelFilterOptions {

    private Optional<String> startingPointCity;
    private Optional<String> endingPointCity;
    private Optional<String> departureBefore;
    private Optional<String> departureAfter;
    private Optional<Integer> minFreeSeats;
    private Optional<String> driverUsername;
    private Optional<String> commentContains;
    private Optional<Integer> statusId;
    private Optional<Integer> driverId;
    private Optional<Integer> passengerId;
    private Optional<String> sortBy;
    private Optional<String> sortOrder;

    public TravelFilterOptions() {
        this(null, null, null, null, null,
                null, null, null, null, null);
    }

    public TravelFilterOptions(String startingPointCity,
                               String endingPointCity,
                               String departureBefore,
                               String departureAfter,
                               Integer minFreeSeats,
                               String driverUsername,
                               String commentContains,
                               Integer statusId,
                               String sortBy,
                               String sortOrder) {
        this.startingPointCity = Optional.ofNullable(startingPointCity);
        this.endingPointCity = Optional.ofNullable(endingPointCity);
        this.departureBefore = Optional.ofNullable(departureBefore);
        this.departureAfter = Optional.ofNullable(departureAfter);
        this.minFreeSeats = Optional.ofNullable(minFreeSeats);
        this.driverUsername = Optional.ofNullable(driverUsername);
        this.commentContains = Optional.ofNullable(commentContains);
        this.statusId = Optional.ofNullable(statusId);
        this.sortBy = Optional.ofNullable(sortBy);
        this.sortOrder = Optional.ofNullable(sortOrder);
    }

    public Optional<String> getStartingPointCity() {
        return startingPointCity;
    }

    public Optional<String> getEndingPointCity() {
        return endingPointCity;
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

    public void setStatusId(Optional<Integer> statusId) {
        this.statusId = statusId;
    }

    public Optional<Integer> getDriverId() {
        return driverId;
    }

    public Optional<Integer> getPassengerId() {
        return passengerId;
    }

    public Optional<String> getSortBy() {
        return sortBy;
    }

    public Optional<String> getSortOrder() {
        return sortOrder;
    }

    public void setDriverId(Optional<Integer> driverId) {
        this.driverId = driverId;
    }

    public void setPassengerId(Optional<Integer> passengerId) {
        this.passengerId = passengerId;
    }
}
