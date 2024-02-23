package com.tripweaver.models;

import jakarta.persistence.*;

@Entity
@Table(name = "travel_statuses")
public class TravelStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "travel_status_id")
    private int travelStatusId;

    @Column(name = "status_name")
    private String statusName;

    public TravelStatus() {
    }

    public int getTravelStatusId() {
        return travelStatusId;
    }

    public void setTravelStatusId(int travelStatusId) {
        this.travelStatusId = travelStatusId;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
}
