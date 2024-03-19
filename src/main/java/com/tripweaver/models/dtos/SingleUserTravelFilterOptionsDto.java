package com.tripweaver.models.dtos;

public class SingleUserTravelFilterOptionsDto extends TravelFilterOptionsDto {
    private Integer driverId;
    private Integer passengerId;
    public SingleUserTravelFilterOptionsDto(String startingPointCity,
                                            String endingPointCity,
                                            String departureBefore,
                                            String departureAfter,
                                            Integer minFreeSeats,
                                            String driverUsername,
                                            String commentContains,
                                            String sortBy,
                                            String sortOrder,
                                            Integer driverId,
                                            Integer passengerId) {
        super(startingPointCity, endingPointCity, departureBefore, departureAfter,
                minFreeSeats, driverUsername, commentContains, sortBy, sortOrder);
        this.driverId = driverId;
        this.passengerId = passengerId;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public Integer getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Integer passengerId) {
        this.passengerId = passengerId;
    }
}
