package com.tripweaver.services;

import com.tripweaver.models.Avatar;
import com.tripweaver.repositories.contracts.AvatarRepository;
import com.tripweaver.services.contracts.AvatarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AvatarServiceImpl implements AvatarService {


    private final AvatarRepository avatarRepository;

    @Autowired
    public AvatarServiceImpl(AvatarRepository avatarRepository) {
        this.avatarRepository = avatarRepository;
    }

    @Override
    public Avatar createAvatar(Avatar avatar) {
        return avatarRepository.createAvatar(avatar);
    }

    @Override
    public Avatar getDefaultAvatar() {
        return avatarRepository.getDefaultAvatar();
    }

    /*Ilia*/
    @Override
    public String uploadPictureToCloudinary(MultipartFile multipartFile) {
        return avatarRepository.uploadPictureToCloudinary(multipartFile);
    }
    /*Ilia*/
    @Override
    public Avatar getAvatarById(int id) {
        return avatarRepository.getAvatarById(id);
    }
}
