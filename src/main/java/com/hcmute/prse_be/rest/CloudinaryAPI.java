package com.hcmute.prse_be.rest;

import com.hcmute.prse_be.dtos.UploadingVideoCache;
import com.hcmute.prse_be.dtos.UploadingVideoDetail;
import com.hcmute.prse_be.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/upload")
public class CloudinaryAPI {
    private final CloudinaryService videoUploadService;
    @Autowired
    public CloudinaryAPI(CloudinaryService videoUploadService) {
        this.videoUploadService = videoUploadService;
    }

//    @PostMapping("/preview-video")
//    public ResponseEntity<UploadingVideoDetail> uploadVideo(@RequestParam("instructorId") String instructorId,
//                                                            @RequestParam("title") String title,
//                                                            @RequestParam("file") MultipartFile file,
//                                                            @RequestParam("folder") String folderName) {
//        if (file.isEmpty()) {
//            return ResponseEntity.badRequest().body(new UploadingVideoDetail("400", "File Null"));
//        }
//        String threadId = UUID.randomUUID().toString();
//        UploadingVideoDetail uploadStatus = new UploadingVideoDetail(threadId, "PENDING");
//        uploadStatus.setInstructorId(instructorId);
//        uploadStatus.setTitle(title);
//        UploadingVideoCache.getInstance().getUploadingVideo().put(threadId, uploadStatus);
//
//        CompletableFuture.supplyAsync(() -> {
//            try {
//                // Thực hiện upload video toàn bộ
//                Map<String, Object> uploadResult = videoUploadService.uploadVideo(file, folderName);
//                uploadStatus.setStatus("COMPLETED");
//
//                // Log đường URL của video
//                String videoUrl = (String) uploadResult.get("url");
//                System.out.println("Video uploaded successfully. URL: " + videoUrl);
//
//                // Cập nhật uploadStatus với kết quả
//                uploadStatus.setUploadResult(uploadResult);
//                return uploadResult;
//            } catch (IOException e) {
//                uploadStatus.setStatus("FAILED");
//                uploadStatus.setErrorMessage(e.getMessage());
//                throw new CompletionException(e);
//            } finally {
//                // Xóa uploadStatus khỏi danh sách sau khi hoàn tất hoặc thất bại
//                UploadingVideoCache.getInstance().getUploadingVideo().remove(threadId);
//            }
//        });
//        return ResponseEntity.ok(uploadStatus);
//    }
//    @PostMapping("/video")
//    public ResponseEntity<UploadingVideoDetail> uploadVideo(@RequestParam("instructorId") String instructorId,
//                                                            @RequestParam("title") String title,
//                                                            @RequestParam("file") MultipartFile file,
//                                                            @RequestParam("course") String courseId,
//                                                            @RequestParam("chapter") String chapterId,
//                                                            @RequestParam("lesson") String lessonId) {
//        if (file.isEmpty()) {
//            return ResponseEntity.badRequest().body(new UploadingVideoDetail("400", "File Null"));
//        }
//
//        // Construct the folder path
//        String folderName = String.format("%s/%s/%s", courseId, chapterId, lessonId);
//        String threadId = UUID.randomUUID().toString();
//        UploadingVideoDetail uploadStatus = new UploadingVideoDetail(threadId, "PENDING");
//        uploadStatus.setInstructorId(instructorId);
//        uploadStatus.setTitle(title);
//        UploadingVideoCache.getInstance().getUploadingVideo().put(threadId, uploadStatus);
//
//        CompletableFuture.supplyAsync(() -> {
//            try {
//                // Perform the video upload
//                Map<String, Object> uploadResult = videoUploadService.uploadVideo(file, folderName);
//                uploadStatus.setStatus("COMPLETED");
//
//                // Log the URL of the video
//                String videoUrl = (String) uploadResult.get("url");
//                System.out.println("Video uploaded successfully. URL: " + videoUrl);
//
//                // Update uploadStatus with the result
//                uploadStatus.setUploadResult(uploadResult);
//                return uploadResult;
//            } catch (IOException e) {
//                uploadStatus.setStatus("FAILED");
//                uploadStatus.setErrorMessage(e.getMessage());
//                throw new CompletionException(e);
//            } finally {
//                UploadingVideoCache.getInstance().getUploadingVideo().remove(threadId);
//            }
//        });
//
//        return ResponseEntity.ok(uploadStatus);
//    }

    @GetMapping("/status/{threadId}")
    public ResponseEntity<UploadingVideoDetail> getUploadStatus(@PathVariable String threadId) {
        // Retrieve the upload status from the singleton cache
        UploadingVideoCache cache = UploadingVideoCache.getInstance();
        UploadingVideoDetail status = cache.getUploadingVideo().get(threadId);

        if (status == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(status);
    }

    @GetMapping("/getAllStatuses")
    public ResponseEntity<Map<String, UploadingVideoDetail>> getAllThread() {
        UploadingVideoCache cache = UploadingVideoCache.getInstance();
        return ResponseEntity.ok(cache.getUploadingVideo());
    }

    @GetMapping("/status/instructor/{instructorId}")
    public ResponseEntity<List<UploadingVideoDetail>> getUploadStatusesByInstructor(@PathVariable String instructorId) {
        UploadingVideoCache cache = UploadingVideoCache.getInstance();
        List<UploadingVideoDetail> statuses = cache.getUploadingVideo().values().stream()
                .filter(status -> instructorId.equals(status.getInstructorId()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(statuses);
    }
}
