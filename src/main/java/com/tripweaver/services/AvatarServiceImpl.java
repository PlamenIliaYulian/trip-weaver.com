package com.tripweaver.services;

import com.tripweaver.models.Avatar;
import com.tripweaver.services.contracts.AvatarService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AvatarServiceImpl implements AvatarService {
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
