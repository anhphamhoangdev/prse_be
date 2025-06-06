package com.hcmute.prse_be.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface CloudinaryService {
    String uploadImage(MultipartFile file, String folderName) throws IOException;

    String uploadFile(byte[] fileData, String folderName, String filePath) throws IOException;

    Map uploadVideo(MultipartFile file, String folderName) throws IOException;

    Map uploadVideoFromBytes(byte[] finalFileData, String finalOriginalFilename, String finalContentType, String folderName);
}
