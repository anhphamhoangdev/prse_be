package com.hcmute.prse_be.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface CloudinaryService {
    public Map uploadFile(MultipartFile file, String folderName) throws IOException;

    public Map<String, Object> uploadVideo(MultipartFile file, String folderName) throws IOException;

}
