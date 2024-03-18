package com.tripweaver.repositories.contracts;

import com.tripweaver.models.CarPicture;

public interface CarPictureRepository extends Uploadable {

    CarPicture createCarPicture(CarPicture carPicture);

    CarPicture getDefaultCarPicture();

    CarPicture getCarPictureById(int id);
}
