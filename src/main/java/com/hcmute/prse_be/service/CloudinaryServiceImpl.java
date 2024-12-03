package com.hcmute.prse_be.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryServiceImpl implements CloudinaryService{
    private final Cloudinary cloudinary;

    public CloudinaryServiceImpl(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public String uploadImage(MultipartFile file, String folderName) throws IOException {

        Map result = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap(
                        "folder", folderName
                ));
        return (String) result.get("url");

    }

    @Override
    public Map uploadVideo(MultipartFile file, String folderName) throws IOException {
        if (file.isEmpty()) {
            LogService.getgI().info("File is empty");
            throw new IllegalArgumentException("File is empty");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("video/")) {
            LogService.getgI().info("Invalid file type");
            throw new IllegalArgumentException("Invalid file type");
        }
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap(
                        "resource_type", "video",
                        "folder", folderName
                ));
        LogService.getgI().info("Video uploaded successfully. URL: " + uploadResult.get("url"));
        return uploadResult;
    }
}
