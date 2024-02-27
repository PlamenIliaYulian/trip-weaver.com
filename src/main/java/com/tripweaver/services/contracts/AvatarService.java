package com.tripweaver.services.contracts;

import com.tripweaver.models.Avatar;
import org.springframework.web.multipart.MultipartFile;

public interface AvatarService {

    /*Plamen*/
    Avatar createAvatar(Avatar avatar);

    /*Yuli - DONE*/
    Avatar getDefaultAvatar();

    /*Ilia*/
    String uploadPictureToCloudinary(MultipartFile multipartFile);
    /*Ilia*/
    Avatar getAvatarById(int id);
}
