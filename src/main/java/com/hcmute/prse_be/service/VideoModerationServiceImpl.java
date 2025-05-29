package com.hcmute.prse_be.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcmute.prse_be.config.Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class VideoModerationServiceImpl implements VideoModerationService{

    private String openaiApiKey = Config.getParam("ai","vision_4_apikey");

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Simplified inner class - chỉ giữ các thuộc tính cần thiết
    public static class FrameAnalysis {
        private int frameNumber;
        private boolean approved;
        private String content;
        private double timestamp;

        public FrameAnalysis() {}

        public FrameAnalysis(int frameNumber, boolean approved, String content, double timestamp) {
            this.frameNumber = frameNumber;
            this.approved = approved;
            this.content = content;
            this.timestamp = timestamp;
        }

        // Getters và Setters
        public int getFrameNumber() { return frameNumber; }
        public void setFrameNumber(int frameNumber) { this.frameNumber = frameNumber; }

        public boolean isApproved() { return approved; }
        public void setApproved(boolean approved) { this.approved = approved; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }

        public double getTimestamp() { return timestamp; }
        public void setTimestamp(double timestamp) { this.timestamp = timestamp; }
    }

    // Simplified Response class
    public static class VideoModerationResult {
        private boolean approved;
        private String overallReason;
        private int totalFramesAnalyzed;
        private List<FrameAnalysis> frameAnalyses;

        public VideoModerationResult() {}

        public VideoModerationResult(boolean approved, String overallReason,
                                     int totalFramesAnalyzed, List<FrameAnalysis> frameAnalyses) {
            this.approved = approved;
            this.overallReason = overallReason;
            this.totalFramesAnalyzed = totalFramesAnalyzed;
            this.frameAnalyses = frameAnalyses != null ? frameAnalyses : new ArrayList<>();
        }

        // Getters và Setters
        public boolean isApproved() { return approved; }
        public void setApproved(boolean approved) { this.approved = approved; }

        public String getOverallReason() { return overallReason; }
        public void setOverallReason(String overallReason) { this.overallReason = overallReason; }

        public int getTotalFramesAnalyzed() { return totalFramesAnalyzed; }
        public void setTotalFramesAnalyzed(int totalFramesAnalyzed) { this.totalFramesAnalyzed = totalFramesAnalyzed; }

        public List<FrameAnalysis> getFrameAnalyses() { return frameAnalyses; }
        public void setFrameAnalyses(List<FrameAnalysis> frameAnalyses) { this.frameAnalyses = frameAnalyses; }
    }
    private String escapeHtml(String text) {
        if (text == null) return "";
        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }


    /**
     * Main method để trả về response format mong muốn
     */
    public String moderateVideoCustomFormat(String videoUrl) {
        try {
            VideoModerationResult result = moderateVideoDetailed(videoUrl);

            // Tạo response_fromai
            String responseFromAi = String.format("Approved: %s<br/>Description: %s",
                    result.isApproved() ? "True" : "False",
                    result.getOverallReason());

            // Tạo HTML content cho từng frame
            StringBuilder contentBuilder = new StringBuilder();
            for (FrameAnalysis analysis : result.getFrameAnalyses()) {
                contentBuilder
                        .append("<p><strong>Frame ")
                        .append(analysis.getFrameNumber())
                        .append(" - ")
                        .append(analysis.isApproved() ? "Phù hợp" : "Không phù hợp")
                        .append("</p>\n<p>")
                        .append(escapeHtml(analysis.getContent()))
                        .append("</p>\n");
            }

            // Tạo JSON response
            Map<String, String> response = new HashMap<>();
            response.put("response_fromai", responseFromAi);
            response.put("content", contentBuilder.toString());

            return objectMapper.writeValueAsString(response);

        } catch (Exception e) {
            log.error("Error creating custom format response: ", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("response_fromai", "Approved: False<br/>Description: System error: " + e.getMessage());
            errorResponse.put("content", "<p>Cannot analyze the video due to a system error</p>");

            try {
                return objectMapper.writeValueAsString(errorResponse);
            } catch (Exception jsonEx) {
                return "{\"response_fromai\": \"Approved: False<br/>Description: System error\", \"content\": \"<p>Cannot analyze video</p>\"}";
            }
        }
    }


    /**
     * Core analysis method - đã được tối ưu
     */
    private VideoModerationResult moderateVideoDetailed(String videoUrl) {
        try {
            log.info("Bắt đầu kiểm duyệt video: {}", videoUrl);

            List<String> frameBase64List = extractFramesFromVideo(videoUrl);

            if (frameBase64List.isEmpty()) {
                log.warn("Không thể trích xuất frames từ video: {}", videoUrl);
                return new VideoModerationResult(
                        false,
                        "Không thể trích xuất frames từ video hoặc video không hợp lệ",
                        0,
                        new ArrayList<>()
                );
            }

            List<FrameAnalysis> frameAnalyses = new ArrayList<>();
            boolean hasViolations = false;

            for (int i = 0; i < frameBase64List.size(); i++) {
                log.info("Đang phân tích frame {}/{}", i + 1, frameBase64List.size());

                double timestamp = i * 10.0;
                FrameAnalysis frameAnalysis = analyzeFrameSimplified(frameBase64List.get(i), i + 1, timestamp);
                frameAnalyses.add(frameAnalysis);

                if (!frameAnalysis.isApproved()) {
                    hasViolations = true;
                }
            }

            String finalReason = hasViolations ?
                    "Video chứa nội dung không phù hợp cho môi trường giáo dục" :
                    String.format("Video phù hợp cho môi trường giáo dục - đã phân tích %d frames", frameBase64List.size());

            return new VideoModerationResult(
                    !hasViolations,
                    finalReason,
                    frameBase64List.size(),
                    frameAnalyses
            );

        } catch (Exception e) {
            log.error("Lỗi kiểm duyệt video: ", e);
            return new VideoModerationResult(
                    false,
                    "Lỗi hệ thống: " + e.getMessage(),
                    0,
                    new ArrayList<>()
            );
        }
    }

    /**
     * Simplified frame analysis - chỉ lấy thông tin cần thiết
     */
    private FrameAnalysis analyzeFrameSimplified(String frameBase64, int frameNumber, double timestamp) {
        try {
            String requestBody = """
                {
                    "model": "gpt-4o",
                    "messages": [
                        {
                            "role": "user",
                            "content": [
                                {
                                    "type": "text",
                                    "text": "Phân tích frame %d của video (tại %.1fs). Kiểm tra tính phù hợp cho giáo dục và mô tả nội dung. Trả về JSON: {\\"approved\\": true/false, \\"content\\": \\"mô tả chi tiết nội dung\\"}"
                                },
                                {
                                    "type": "image_url",
                                    "image_url": {
                                        "url": "%s"
                                    }
                                }
                            ]
                        }
                    ],
                    "max_tokens": 400,
                    "temperature": 0.1
                }
                """.formatted(frameNumber, timestamp, frameBase64);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + openaiApiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(
                    "https://api.openai.com/v1/chat/completions",
                    entity,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                return parseSimplifiedResponse(response.getBody(), frameNumber, timestamp);
            } else {
                log.error("Lỗi API khi phân tích frame {}: {}", frameNumber, response.getStatusCode());
                return new FrameAnalysis(
                        frameNumber,
                        false,
                        "Không thể phân tích nội dung do lỗi API",
                        timestamp
                );
            }

        } catch (Exception e) {
            log.error("Lỗi phân tích frame {}: ", frameNumber, e);
            return new FrameAnalysis(
                    frameNumber,
                    false,
                    "Không thể phân tích nội dung do lỗi hệ thống",
                    timestamp
            );
        }
    }

    /**
     * Parse simplified response
     */
    private FrameAnalysis parseSimplifiedResponse(String apiResponse, int frameNumber, double timestamp) {
        try {
            JsonNode rootNode = objectMapper.readTree(apiResponse);
            JsonNode choicesNode = rootNode.get("choices");

            if (choicesNode != null && choicesNode.isArray() && choicesNode.size() > 0) {
                JsonNode messageNode = choicesNode.get(0).get("message");
                if (messageNode != null) {
                    String rawContent = messageNode.get("content").asText().trim();

                    // Clean Markdown format if exists: ```json ... ```
                    if (rawContent.startsWith("```json") || rawContent.startsWith("```")) {
                        rawContent = rawContent.replaceAll("```json", "")
                                .replaceAll("```", "")
                                .trim();
                    }

                    // Try parse JSON
                    try {
                        JsonNode analysisNode = objectMapper.readTree(rawContent);
                        boolean approved = analysisNode.get("approved").asBoolean();
                        String frameContent = analysisNode.has("content") ? analysisNode.get("content").asText() : "Không có mô tả";

                        return new FrameAnalysis(frameNumber, approved, frameContent, timestamp);

                    } catch (Exception parseEx) {
                        // Nếu không parse được thì fallback
                        log.warn("Không thể parse JSON, dùng văn bản mô tả thô cho frame {}", frameNumber);
                        boolean approved = !rawContent.toLowerCase().contains("false");
                        return new FrameAnalysis(frameNumber, approved, rawContent, timestamp);
                    }
                }
            }

        } catch (Exception e) {
            log.error("Lỗi parse response cho frame {}: ", frameNumber, e);
        }

        return new FrameAnalysis(frameNumber, false, "Không thể xác định nội dung", timestamp);
    }

    // Giữ nguyên các method extract frames (không thay đổi)
    private List<String> extractFramesFromVideo(String videoUrl) {
        List<String> frameBase64List = new ArrayList<>();
        String tempDir = System.getProperty("java.io.tmpdir");
        String videoFileName = "video_" + System.currentTimeMillis() + ".mp4";
        String videoPath = tempDir + "/" + videoFileName;
        String framePattern = tempDir + "/frame_%03d.jpg";

        try {
            log.info("Bắt đầu extract frames từ video: {}", videoUrl);

            downloadVideo(videoUrl, videoPath);

            ProcessBuilder processBuilder = new ProcessBuilder(
                    "ffmpeg",
                    "-i", videoPath,
                    "-vf", "fps=1/10",
                    "-frames:v", "6",
                    "-q:v", "2",
                    "-y",
                    framePattern
            );

            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            try (var reader = new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    log.debug("FFmpeg output: {}", line);
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                log.error("FFmpeg failed with exit code: {}", exitCode);
                return frameBase64List;
            }

            for (int i = 1; i <= 6; i++) {
                String framePath = tempDir + "/frame_" + String.format("%03d", i) + ".jpg";
                java.io.File frameFile = new java.io.File(framePath);

                if (frameFile.exists()) {
                    String base64Frame = convertImageToBase64(framePath);
                    if (base64Frame != null) {
                        frameBase64List.add("data:image/jpeg;base64," + base64Frame);
                    }
                }
            }

            log.info("Đã extract {} frames từ video", frameBase64List.size());

        } catch (Exception e) {
            log.error("Lỗi extract frames từ video: ", e);
        } finally {
            cleanupTempFiles(videoPath, tempDir + "/frame_*.jpg");
        }

        return frameBase64List;
    }

    private void downloadVideo(String videoUrl, String outputPath) throws Exception {
        log.info("Downloading video từ URL: {}", videoUrl);

        try (java.io.InputStream in = new java.net.URL(videoUrl).openStream();
             java.io.FileOutputStream out = new java.io.FileOutputStream(outputPath)) {

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }

        log.info("Đã download video thành công: {}", outputPath);
    }

    private String convertImageToBase64(String imagePath) {
        try {
            java.io.File imageFile = new java.io.File(imagePath);
            if (!imageFile.exists()) {
                log.warn("Image file không tồn tại: {}", imagePath);
                return null;
            }

            byte[] imageBytes = java.nio.file.Files.readAllBytes(imageFile.toPath());
            return java.util.Base64.getEncoder().encodeToString(imageBytes);

        } catch (Exception e) {
            log.error("Lỗi convert image sang base64: {}", imagePath, e);
            return null;
        }
    }

    private void cleanupTempFiles(String videoPath, String framePattern) {
        try {
            java.io.File videoFile = new java.io.File(videoPath);
            if (videoFile.exists()) {
                videoFile.delete();
                log.debug("Đã xóa temp video file: {}", videoPath);
            }

            String tempDir = System.getProperty("java.io.tmpdir");
            java.io.File dir = new java.io.File(tempDir);
            java.io.File[] frameFiles = dir.listFiles((d, name) -> name.startsWith("frame_") && name.endsWith(".jpg"));

            if (frameFiles != null) {
                for (java.io.File file : frameFiles) {
                    file.delete();
                    log.debug("Đã xóa temp frame file: {}", file.getName());
                }
            }

        } catch (Exception e) {
            log.warn("Lỗi cleanup temp files: ", e);
        }
    }
}