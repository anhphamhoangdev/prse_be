package com.hcmute.prse_be.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hcmute.prse_be.config.Config;
import com.hcmute.prse_be.constants.ApiPaths;
import com.hcmute.prse_be.dtos.WebSocketMessage;
import com.hcmute.prse_be.repository.CourseRepository;
import com.hcmute.prse_be.response.Response;
import com.hcmute.prse_be.service.CourseService;
import com.hcmute.prse_be.service.LogService;
import com.hcmute.prse_be.service.WebSocketService;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vn.payos.PayOS;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping(ApiPaths.TEST_API)
public class TestAPI {

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    CourseService courseService;
    @Autowired
    private PayOS payOS;

    @Autowired
    WebSocketService webSocketService;

    String certificatePath = Config.getParam("certificate","base_file");

    @PostMapping(ApiPaths.TEST_SEND_NOTIFY_INSTRUCTOR_ID)
    public ResponseEntity<String> testNotification(@PathVariable Long instructorId) {
        LogService.getgI().info("[TestAPI] testNotification to InstructorId: " + instructorId);

        try {
            // Tạo một message test
            WebSocketMessage message = WebSocketMessage.info(
                    "Xin chào! Đây là thông báo test.", null
            );
            webSocketService.sendToInstructor(instructorId, "/uploads",message);

            return ResponseEntity.ok("Notification sent successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error sending notification: " + e.getMessage());
        }
    }

    @PostMapping("/createCertificate")
    public ResponseEntity<String> createCertificate(@RequestParam String userName) {
        try {
            // Đường dẫn tới file certificate template
            String certificateTemplatePath = certificatePath;

            // Tạo thư mục để lưu certificates nếu chưa tồn tại
            String certificateDir = "src/main/resources/static/certificates";
            File directory = new File(certificateDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Đặt tên file đầu ra
            String fileName = "certificate_" + userName.replaceAll("\\s+", "_") + ".png";
            String outputPath = certificateDir + File.separator + fileName;

            // Thêm tên và lời chúc mừng vào chứng chỉ
            addTextToCertificate(certificateTemplatePath, outputPath, userName);

            // Trả về đường dẫn tương đối để truy cập file từ web
            return ResponseEntity.ok("/certificates/" + fileName);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Lỗi tạo chứng chỉ: " + e.getMessage());
        }
    }

    /**
     * Thêm tên và lời chúc mừng vào chứng chỉ
     */
    private void addTextToCertificate(String certificateTemplatePath, String outputPath, String userName) {
        try {
            // Đọc hình ảnh template
            File templateFile = new File(certificateTemplatePath);
            if (!templateFile.exists()) {
                throw new IOException("File certificate template không tồn tại: " + certificateTemplatePath);
            }

            BufferedImage certificateTemplate = ImageIO.read(templateFile);

            // Tạo đối tượng Graphics2D để chỉnh sửa hình ảnh
            Graphics2D g2d = certificateTemplate.createGraphics();

            // Bật anti-aliasing để text mịn hơn
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // ----- Thêm tên người nhận chứng chỉ -----
            Font nameFont = new Font("Sansita", Font.BOLD | Font.ITALIC, 100);
            g2d.setFont(nameFont);
            g2d.setColor(new Color(28, 78, 216));

            // Căn giữa text
            int textWidth = g2d.getFontMetrics().stringWidth(userName);
            int x = (certificateTemplate.getWidth() - textWidth) / 2;

            // Vị trí y cho tên - thường ở khoảng giữa chứng chỉ
            int nameY = (int)(certificateTemplate.getHeight() * 0.51);

            // Vẽ tên lên hình ảnh
            g2d.drawString(userName, x, nameY);

            // Font cho phần còn lại
            Font normalFont = new Font("Calibri", Font.ITALIC, 34);
            Font boldItalicFont = new Font("Calibri", Font.BOLD | Font.ITALIC, 34);
            g2d.setColor(new Color(28, 78, 216));  // Màu xanh

            String part1 = "Has successfully completed the course ";
            String courseName = "'Kiến Thức Nhập Môn IT ReactJS: Từ Zero đến Hero ReactJS: Từ Zero đến Hero ReactJS: Từ Zero đến Hero'";
            String part2 = " instructed by ABC.";

            // Tính chiều rộng khả dụng (80% chiều rộng của certificate)
            int availableWidth = (int)(certificateTemplate.getWidth() * 0.8);
            int startX = (certificateTemplate.getWidth() - availableWidth) / 2;
            int y = (int)(certificateTemplate.getHeight() * 0.6);
            int lineHeight = g2d.getFontMetrics(normalFont).getHeight();

            // Vẽ phần 1
            g2d.setFont(normalFont);
            g2d.drawString(part1, startX, y);
            int currentX = startX + g2d.getFontMetrics().stringWidth(part1);

            // Xử lý wrap text cho courseName
            g2d.setFont(boldItalicFont);
            FontMetrics fm = g2d.getFontMetrics();
            int spaceWidth = fm.stringWidth(" ");

            String[] words = courseName.split("\\s+");
            int currentLineWidth = 0;

            for (int i = 0; i < words.length; i++) {
                int wordWidth = fm.stringWidth(words[i]);

                // Nếu thêm từ này vượt quá chiều rộng khả dụng, xuống dòng
                if (currentX + currentLineWidth + wordWidth > startX + availableWidth) {
                    // Xuống dòng
                    y += lineHeight;
                    g2d.drawString(currentLineWidth > 0 ? "" : words[i], startX, y);
                    currentX = startX + (currentLineWidth > 0 ? 0 : wordWidth + spaceWidth);
                    currentLineWidth = currentLineWidth > 0 ? wordWidth + spaceWidth : 0;
                } else {
                    // Tiếp tục trên dòng hiện tại
                    g2d.drawString(words[i] + " ", currentX + currentLineWidth, i == 0 ? y : y);
                    currentLineWidth += wordWidth + spaceWidth;
                }
            }

            // Cập nhật vị trí x mới sau khi đã vẽ courseName
            currentX = currentX + currentLineWidth;

            // Vẽ phần 2
            g2d.setFont(normalFont);
            // Nếu không đủ chỗ cho part2, xuống dòng
            if (currentX + fm.stringWidth(part2) > startX + availableWidth) {
                y += lineHeight;
                currentX = startX;
            }
            g2d.drawString(part2, currentX, y);

            // Giải phóng tài nguyên
            g2d.dispose();

            // Lưu hình ảnh đã chỉnh sửa
            ImageIO.write(certificateTemplate, "png", new File(outputPath));

        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi thêm text vào certificate: " + e.getMessage(), e);
        }
    }




}
