package com.tripweaver.repositories;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.tripweaver.exceptions.InvalidOperationException;
import com.tripweaver.repositories.contracts.Uploadable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public class UploadableImpl implements Uploadable {

    private final String CLOUDINARY_URL = "cloudinary://242857587276945:B5ODyO381gN-4aFLKDNVcrAFzxM@dol3hflxs";

    @Override
    public String uploadPictureToCloudinary(MultipartFile multipartFile) {
        Cloudinary cloudinary = new Cloudinary(CLOUDINARY_URL);
        cloudinary.config.secure = true;
        try {
            Map params1 = ObjectUtils.asMap(
                    "use_filename", true,
                    "unique_filename", true,
                    "overwrite", false
            );

            return cloudinary
                    .uploader()
                    .upload(multipartFile.getBytes(), params1)
                    .get("secure_url")
                    .toString();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
