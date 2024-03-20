package com.tripweaver.services;

import com.tripweaver.exceptions.InvalidOperationException;
import com.tripweaver.models.CarPicture;
import com.tripweaver.repositories.contracts.CarPictureRepository;
import com.tripweaver.services.contracts.CarPictureService;
import com.tripweaver.services.helpers.ValidationHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
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
        ValidationHelper.checkPictureFileSize(multipartFile);
        ValidationHelper.checkFileExtension(multipartFile);
        return carPictureRepository.uploadPictureToCloudinary(multipartFile);
    }

    @Override
    public CarPicture getCarPictureById(int id) {
        return carPictureRepository.getCarPictureById(id);
    }

}
