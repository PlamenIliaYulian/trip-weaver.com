package com.tripweaver.services;

import com.tripweaver.helpers.TestHelpers;
import com.tripweaver.models.Avatar;
import com.tripweaver.repositories.contracts.AvatarRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
public class AvatarServiceTests {

    @Mock
    AvatarRepository avatarRepository;
    @InjectMocks
    AvatarServiceImpl avatarService;


    @Test
    public void createAvatar_Should_CallRepository() {
        Avatar avatar = TestHelpers.createMockAvatar();

        avatarService.createAvatar(avatar);

        Mockito.verify(avatarRepository, Mockito.times(1))
                .createAvatar(avatar);
    }

    @Test
    public void uploadPictureToCloudinary_Should_CallRepository() {
        MultipartFile mockMultipartFile = TestHelpers.createMockMultipartFile();

        avatarService.uploadPictureToCloudinary(mockMultipartFile);

        Mockito.verify(avatarRepository, Mockito.times(1))
                .uploadPictureToCloudinary(mockMultipartFile);
    }

    @Test
    public void getAvatarById_Should_CallRepository() {
        avatarService.getAvatarById(Mockito.anyInt());

        Mockito.verify(avatarRepository, Mockito.times(1))
                .getAvatarById(Mockito.anyInt());
    }

    @Test
    public void getDefaultAvatar_Should_CallRepository() {
        avatarService.getDefaultAvatar();

        Mockito.verify(avatarRepository, Mockito.times(1))
                .getDefaultAvatar();
    }

}
