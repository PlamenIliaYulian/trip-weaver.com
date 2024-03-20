package com.tripweaver.services;

import com.tripweaver.exceptions.InvalidOperationException;
import com.tripweaver.models.Avatar;
import com.tripweaver.repositories.contracts.AvatarRepository;
import com.tripweaver.services.contracts.AvatarService;
import com.tripweaver.services.helpers.ValidationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
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

    @Override
    public String uploadPictureToCloudinary(MultipartFile multipartFile) {
        ValidationHelper.checkPictureFileSize(multipartFile);
        ValidationHelper.checkFileExtension(multipartFile);
        return avatarRepository.uploadPictureToCloudinary(multipartFile);
    }

    @Override
    public Avatar getAvatarById(int id) {
        return avatarRepository.getAvatarById(id);
    }
}
