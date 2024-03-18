package com.tripweaver.repositories.contracts;

import org.springframework.web.multipart.MultipartFile;

public interface Uploadable {

    String uploadPictureToCloudinary(MultipartFile multipartFile);


}
