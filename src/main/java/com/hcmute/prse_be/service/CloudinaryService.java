package com.hcmute.prse_be.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface CloudinaryService {
    String uploadImage(MultipartFile file, String folderName) throws IOException;

    Map uploadVideo(MultipartFile file, String folderName) throws IOException;
}
