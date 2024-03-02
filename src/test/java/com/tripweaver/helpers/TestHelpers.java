package com.tripweaver.helpers;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class TestHelpers {

    public static MultipartFile createMockMultipartFile() {
        return new MockMultipartFile("String",new byte[10]);
    }


}
