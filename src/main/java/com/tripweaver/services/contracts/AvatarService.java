package com.tripweaver.services.contracts;

import com.tripweaver.models.Avatar;
import com.tripweaver.repositories.contracts.Uploadable;
import org.springframework.web.multipart.MultipartFile;

public interface AvatarService {

    Avatar createAvatar(Avatar avatar);

    Avatar getDefaultAvatar();

    Avatar getAvatarById(int id);

    String uploadPictureToCloudinary(MultipartFile multipartFile);
}
