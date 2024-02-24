package com.tripweaver.services.contracts;

import com.tripweaver.models.Avatar;
import org.springframework.web.multipart.MultipartFile;

public interface AvatarService {

    /*ToDo Plamen*/
    Avatar createAvatar(Avatar avatar);

    /*ToDo Yuli*/
    Avatar getDefaultAvatar();

    /*ToDo Ilia*/
    String uploadPictureToCloudinary(MultipartFile multipartFile);
}
