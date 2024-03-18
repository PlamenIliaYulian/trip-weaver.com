package com.tripweaver.services;

import com.tripweaver.models.CarPicture;
import com.tripweaver.repositories.contracts.CarPictureRepository;
import com.tripweaver.services.contracts.CarPictureService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CarPictureServiceImpl implements CarPictureService {

    private final CarPictureRepository carPictureRepository;

    public CarPictureServiceImpl(CarPictureRepository carPictureRepository) {
        this.carPictureRepository = carPictureRepository;
    }

    @Override
    public CarPicture createCarPicture(CarPicture carPicture) {
        return carPictureRepository.createCarPicture(carPicture);
    }

    @Override
    public CarPicture getDefaultCarPicture() {
        return carPictureRepository.getDefaultCarPicture();
    }


    @Override
    public String uploadPictureToCloudinary(MultipartFile multipartFile) {
        return carPictureRepository.uploadPictureToCloudinary(multipartFile);
    }

    @Override
    public CarPicture getCarPictureById(int id) {
        return carPictureRepository.getCarPictureById(id);
    }

}
