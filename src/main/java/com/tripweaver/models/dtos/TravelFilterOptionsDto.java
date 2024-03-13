package com.tripweaver.models.dtos;

public class TravelFilterOptionsDto {
    private String startingPointCity;
    private String endingPointCity;
    private String departureBefore;
    private String departureAfter;
    private Integer minFreeSeats;
    private String driverUsername;
    private String commentContains;
    private String sortBy;
    private String sortOrder;


    public TravelFilterOptionsDto(String startingPointCity,
                                  String endingPointCity,
                                  String departureBefore,
                                  String departureAfter,
                                  Integer minFreeSeats,
                                  String driverUsername,
                                  String commentContains,
                                  String sortBy,
                                  String sortOrder) {
        this.startingPointCity = startingPointCity;
        this.endingPointCity = endingPointCity;
        this.departureBefore = departureBefore;
        this.departureAfter = departureAfter;
        this.minFreeSeats = minFreeSeats;
        this.driverUsername = driverUsername;
        this.commentContains = commentContains;
        this.sortBy = sortBy;
        this.sortOrder = sortOrder;
    }

    public String getStartingPointCity() {
        return startingPointCity;
    }

    public void setStartingPointCity(String startingPointCity) {
        this.startingPointCity = startingPointCity;
    }

    public String getEndingPointCity() {
        return endingPointCity;
    }

    public void setEndingPointCity(String endingPointCity) {
        this.endingPointCity = endingPointCity;
    }

    public String getDepartureBefore() {
        return departureBefore;
    }

    public void setDepartureBefore(String departureBefore) {
        this.departureBefore = departureBefore;
    }

    public String getDepartureAfter() {
        return departureAfter;
    }

    public void setDepartureAfter(String departureAfter) {
        this.departureAfter = departureAfter;
    }

    public Integer getMinFreeSeats() {
        return minFreeSeats;
    }

    public void setMinFreeSeats(Integer minFreeSeats) {
        this.minFreeSeats = minFreeSeats;
    }

    public String getDriverUsername() {
        return driverUsername;
    }

    public void setDriverUsername(String driverUsername) {
        this.driverUsername = driverUsername;
    }

    public String getCommentContains() {
        return commentContains;
    }

    public void setCommentContains(String commentContains) {
        this.commentContains = commentContains;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }
}
