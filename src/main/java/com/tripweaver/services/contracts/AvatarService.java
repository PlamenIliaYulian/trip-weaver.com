package com.tripweaver.services.contracts;

import com.tripweaver.models.Avatar;
import org.springframework.web.multipart.MultipartFile;

public interface AvatarService {

    Avatar createAvatar(Avatar avatar);

    Avatar getDefaultAvatar();

    String uploadPictureToCloudinary(MultipartFile multipartFile);

    Avatar getAvatarById(int id);
}
