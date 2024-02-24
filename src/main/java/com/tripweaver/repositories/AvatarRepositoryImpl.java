package com.tripweaver.repositories;

import com.tripweaver.models.Avatar;
import com.tripweaver.repositories.contracts.AvatarRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

@Repository
public class AvatarRepositoryImpl implements AvatarRepository {


    @Override
    public Avatar createAvatar(Avatar avatar) {
        return null;
    }

    @Override
    public Avatar getDefaultAvatar() {
        return null;
    }

    @Override
    public String uploadPictureToCloudinary(MultipartFile multipartFile) {
        return null;
    }
}
