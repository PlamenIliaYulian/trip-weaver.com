package com.tripweaver.repositories.contracts;

import com.tripweaver.models.Avatar;
import org.springframework.web.multipart.MultipartFile;

public interface AvatarRepository {

    Avatar createAvatar(Avatar avatar);

    Avatar getDefaultAvatar();

    String uploadPictureToCloudinary(MultipartFile multipartFile);


    Avatar getAvatarById(int id);
}
