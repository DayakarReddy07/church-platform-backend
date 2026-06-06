package com.church.church_platform.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    // Upload any image
    public String uploadImage(
            MultipartFile file,
            String folder) throws IOException {

        Map result = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", "onebody/" + folder,
                        "resource_type", "auto",

                        // Auto crop to square
                        "gravity", "face",
                        "height", 400,
                        "width", 400,
                        "crop", "fill",
                        "quality", "auto",
                        "fetch_format", "auto"
                )
        );

        return result.get("secure_url").toString();
    }

    // Delete image by URL
    public void deleteImage(String imageUrl) {
        try {
            // Extract public ID from URL
            String publicId = extractPublicId(imageUrl);
            cloudinary.uploader().destroy(
                    publicId,
                    ObjectUtils.emptyMap()
            );
        } catch (Exception e) {
            // Log but don't throw
            System.out.println(
                    "Could not delete image: " + e.getMessage()
            );
        }
    }

    // Extract public ID from Cloudinary URL
    private String extractPublicId(String url) {
        // URL format:
        // https://res.cloudinary.com/cloud/image/upload/v123/onebody/folder/filename.jpg
        String[] parts = url.split("/");
        String filename = parts[parts.length - 1];
        String folder = parts[parts.length - 2];
        String parentFolder = parts[parts.length - 3];
        // Remove extension
        String nameWithoutExt = filename
                .substring(0, filename.lastIndexOf('.'));
        return parentFolder + "/" +
                folder + "/" +
                nameWithoutExt;
    }
}