package com.hcmute.prse_be.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
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
        return (String) result.get("secure_url");

    }

    @Override
    public Map uploadVideo(MultipartFile file, String folderName) throws IOException {
        LogService.getgI().info("uploadVideo starting..." + folderName);

        if (file == null) {
            LogService.getgI().info("File is null");
            throw new IllegalArgumentException("File is null");
        }

        if (file.isEmpty()) {
            LogService.getgI().info("File is empty - size: " + file.getSize());
            throw new IllegalArgumentException("File is empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("video/")) {
            LogService.getgI().info("Invalid file type");
            throw new IllegalArgumentException("Invalid file type");
        }

        try {
            // Đọc byte array và sử dụng phương thức mới
            byte[] fileData = file.getBytes();
            return uploadVideoFromBytes(fileData, file.getOriginalFilename(), file.getContentType(), folderName);
        } catch (Exception e) {
            LogService.getgI().error(e);
            e.printStackTrace();
            throw new IllegalArgumentException("Error when uploading video: " + e.getMessage());
        }
    }

    @Override
    public Map uploadVideoFromBytes(byte[] fileData, String originalFilename, String contentType, String folderName) {
        LogService.getgI().info("uploadVideoFromBytes starting... " + folderName);

        if (fileData == null || fileData.length == 0) {
            LogService.getgI().info("File data is empty");
            throw new IllegalArgumentException("File data is empty");
        }

        if (contentType == null || !contentType.startsWith("video/")) {
            LogService.getgI().info("Invalid file type: " + contentType);
            throw new IllegalArgumentException("Invalid file type");
        }

        try {
            // Tạo file tạm để lưu trữ dữ liệu
            File tempDir = new File(System.getProperty("java.io.tmpdir"), "video_uploads");
            if (!tempDir.exists()) {
                tempDir.mkdirs();
            }

            File tempFile = new File(tempDir, "upload_" + System.currentTimeMillis() + "_" +
                    (originalFilename != null ? originalFilename : "video.mp4"));

            LogService.getgI().info("Saving to temp file: " + tempFile.getAbsolutePath());

            // Ghi byte array vào file tạm
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(fileData);
            }

            LogService.getgI().info("Saved to temp file. Size: " + tempFile.length());

            // Upload file tạm lên Cloudinary
            Map uploadResult = cloudinary.uploader().upload(tempFile,
                    ObjectUtils.asMap(
                            "resource_type", "video",
                            "folder", folderName,
                            "filename_override", originalFilename
                    ));

            LogService.getgI().info("Upload to Cloudinary successful");

            // Xóa file tạm sau khi upload
            if (!tempFile.delete()) {
                tempFile.deleteOnExit();
            }

            return uploadResult;
        } catch (Exception e) {
            LogService.getgI().error(e);
            e.printStackTrace();
            throw new IllegalArgumentException("Error when uploading video: " + e.getMessage());
        }
    }

    @Override
    public String uploadFile(byte[] fileData, String folderName, String filePath) throws IOException {
        Map result = cloudinary.uploader().upload(fileData,
                ObjectUtils.asMap(
                        "public_id", filePath.substring(0, filePath.lastIndexOf('.')),
                        "folder", folderName
                ));
        return (String) result.get("secure_url");
    }


}
