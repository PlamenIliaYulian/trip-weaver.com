package com.tripweaver.models;

import jakarta.persistence.*;

@Entity
@Table(name = "cars_pictures")
public class CarPicture {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "car_picture_id")
    private int carPictureId;

    @Column(name = "car_picture_url")
    private String carPictureUrl;

    public CarPicture() {
    }


    public int getCarPictureId() {
        return carPictureId;
    }

    public void setCarPictureId(int carPictureId) {
        this.carPictureId = carPictureId;
    }

    public String getCarPictureUrl() {
        return carPictureUrl;
    }

    public void setCarPictureUrl(String carPictureUrl) {
        this.carPictureUrl = carPictureUrl;
    }
}
