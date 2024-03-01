package com.tripweaver.services;

import com.tripweaver.repositories.contracts.AvatarRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AvatarServiceTests {

    @Mock
    AvatarRepository avatarRepository;

    @InjectMocks
    AvatarServiceImpl avatarService;

}
