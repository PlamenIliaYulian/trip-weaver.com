package com.tripweaver.services.contracts;

import com.tripweaver.models.CarPicture;
import org.springframework.web.multipart.MultipartFile;

public interface CarPictureService  {


    CarPicture createCarPicture(CarPicture carPicture);
    CarPicture getDefaultCarPicture();
    CarPicture getCarPictureById(int id);
    String uploadPictureToCloudinary(MultipartFile multipartFile);
}
